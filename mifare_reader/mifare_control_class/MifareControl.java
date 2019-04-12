package mifare;

import javax.smartcardio.*;
import java.util.*;

public class MifareControl {
	
	Card card;
	
	private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int a = 0;
        for (byte b : bytes) { // ʹ�ó���ȡ�����ת��
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }
            sb.append(HEX_CHAR[a / 16]);
            sb.append(HEX_CHAR[a % 16]);
        }
        return sb.toString().toUpperCase();
    }
    
    public static byte[] toByteArray(String hexString) {
   	 
  	  hexString = hexString.toLowerCase();
  	  final byte[] byteArray = new byte[hexString.length() / 2];
  	  int k = 0;
  	  for (int i = 0; i < byteArray.length; i++) {//��Ϊ��16���ƣ����ֻ��ռ��4λ��ת�����ֽ���Ҫ����16���Ƶ��ַ�����λ����
  	   byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
  	   byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
  	   byteArray[i] = (byte) (high << 4 | low);
  	   k += 2;
  	  }
  	  return byteArray;
  	 }
    
	/**
	 *��������ʼ��
	 *�ɹ�����CardTerminal
	 *ʧ�ܷ���null
	 */
    public CardTerminal terminalInitial(){
    	try {
			TerminalFactory factory = TerminalFactory.getDefault();//��ȡĬ�ϵĶ���������
			List<CardTerminal> terminals;//����һ��List�����Ŷ�����
			terminals = factory.terminals().list();//�ӹ�����ò��ڵ����ϵĶ������б�
			CardTerminal a = terminals.get(0);
			return a;
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
	/**
	 *��ȡ����������
	 *�ɹ����ض��������ƣ�String��
	 *ʧ�ܷ���null
	 */
    public String getTerminalName(CardTerminal ct){
    	if (ct != null)
    		return ct.toString();
    	else return null;
    }
    
	/**
	 *��ÿ�Ƭ
	 *����terminal����Ҫִ�в�����terminal����
	 *�ɹ��򷵻�Card����
	 *ʧ�ܷ���null
	 */
    public Card getCard(CardTerminal terminal){
    	try {
			terminal.waitForCardPresent(0L);//�ȴ����ÿ�Ƭ
			card = terminal.connect("T=1");//���ӿ�Ƭ
			return card;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
	/**
	 *��ȡmifare��Ƭuidֵ
	 *����card����Ҫִ�в�����card����
	 *�ɹ�����uid��String��
	 *ʧ�ܷ���null
	 */
    public String getCardUID(Card card){
		try {
			CardChannel channel = card.getBasicChannel();//��ͨ��
			CommandAPDU getUID = new CommandAPDU(0xFF,  0xCA, 0x00, 0x00,0x04);//����API��12ҳ
			ResponseAPDU r = channel.transmit(getUID);//����getUIDָ��
//			System.out.println("UID: " + bytesToHexString(r.getData()));
			return bytesToHexString(r.getData());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
	/**
	 *���ָ��������֤mifare��Ƭ��Կ
	 *card����Ҫִ�в�����card����
	 *pwd��string���ͱ�ʾ��16������Կ��String��12���ַ���
	 *sector����Ҫ��֤������
	 *block����Ҫ��֤�ĵĿ飨��������Ӧ��0,1,2,3�飩
	 *keyType����Կ���ͣ�'A' OR 'B'��
	 *����ֵ����֤�ɹ�����true��ʧ�ܷ���false
	 */
    public Boolean pwdAuth(Card card, String pwd, int sector, int block, char keyType){
    	byte[] pwdArray = toByteArray(pwd);
    	int kType = 0;
    	
    	//byte[] pwdArray = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
    	
    	if (keyType == 'A')
    		kType = 0x60;
    	else if (keyType == 'B')
    		kType = 0x61;
    	
        try {
        	CardChannel channel = card.getBasicChannel();
        	CommandAPDU loadPWD = new CommandAPDU(0xFF, 0x82, 0x00, 0x00, pwdArray);//Ȼ����һ��������ԿAPDUָ��~
			ResponseAPDU r1 = channel.transmit(loadPWD);//����loadPWDָ��
			String s = bytesToHexString(r1.getBytes());
			System.out.println(s);
			if (bytesToHexString(r1.getBytes()).equals("6300")){
				System.out.println("��Կ���ش���");
				return false;
			}
			else if (bytesToHexString(r1.getBytes()).equals( "9000"))
				System.out.println("��Կ���سɹ�");
				
			System.out.println("LOAD PWD result: " + bytesToHexString(r1.getBytes()));
		
        
        int local = sector * 4 + block;

        byte[] check = {(byte)0x01,(byte)0x00, (byte)local, (byte)kType, (byte)0x00};//��֤�����ֽڣ���������Ҫ��֤������š���Կ���ͺ���Կ�洢�ĵ�ַ(��Կ��)
        CommandAPDU authPWD = new CommandAPDU(0xFF, 0x86, 0x00, 0x00, check);//����ָ��ͷ�����������������֤APDUָ��.
        ResponseAPDU r2 = channel.transmit(authPWD);//������ָ֤��
        System.out.println("CHECK PWD result: " + bytesToHexString(r2.getBytes()));//��ӡ����ֵ
        if (bytesToHexString(r2.getBytes()).equals( "6300")){
        	System.out.println("��Կ��֤����");
        	return false;
        }
        else if (bytesToHexString(r2.getBytes()).equals("9000")){
        	System.out.println("��Կ��֤�ɹ�");
        	return true;
        }
        
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
        
    }

	/**
	 *��ȡmifare����ָ���������
	 *card����Ҫִ�в�����card����
	 *pwd��string���ͱ�ʾ��16������Կ��String��12���ַ���
	 *sector����Ҫ���в���������
	 *block����Ҫ���в����ĵĿ飨��������Ӧ��0,1,2,3�飩
	 *keyType����Կ���ͣ�'A' OR 'B'��
	 *lenth����Ҫ��ȡ�����ݵ��ֽ��������ó���16��
	 *����ֵ���ɹ�����String�������ݣ�ʧ�ܷ���null
	 */
    public String readData(Card card, String pwd, int sector, int block, char keyType, int lenth){
    	
    	if (!pwdAuth(card, pwd, sector, block, keyType)){
    		System.out.print("��Կ��֤ʧ��");
    		return null;
    	}
    	int local = sector * 4 + block;
    	
    	try {
			CardChannel channel = card.getBasicChannel();
			CommandAPDU getData = new CommandAPDU(0xFF, 0xB0, 0x00,local,lenth);//���������APDUָ��,������ֵ
			ResponseAPDU r3 = channel.transmit(getData);//���Ͷ�����ָ��
			System.out.println("READ data: " + bytesToHexString(r3.getBytes()));//��ӡ����ֵ
			
			return bytesToHexString(r3.getBytes()).substring(0, bytesToHexString(r3.getBytes()).length()-4);
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    
	/**
	 *��mifare����ָ����д��ָ������
	 *card����Ҫִ�в�����card����
	 *pwd��string���ͱ�ʾ��16������Կ��String��12���ַ���
	 *sector����Ҫ���в���������
	 *block����Ҫ���в����ĵĿ飨��������Ӧ��0,1,2,3�飩
	 *keyType����Կ���ͣ�'A' OR 'B'��
	 *data����String���ͱ�ʾ����Ҫд������ݣ�16������16�ֽڣ�
	 */
    public Boolean writeData(Card card, String pwd, int sector, int block, char keyType, String data){
    	
    	if (!pwdAuth(card, pwd, sector, block, keyType)){
    		System.out.print("��Կ��֤ʧ��");
    		return false;
    	}
    	
    	int local = sector * 4 + block; 
    	try {
			CardChannel channel= card.getBasicChannel();
			byte[] byteData = toByteArray(data);
			CommandAPDU updateData = new CommandAPDU(0xFF, 0xD6, 0x00, local, byteData);
			//CommandAPDU updateData = new CommandAPDU(0xFF, 0xD6, 0x00, 0x39, byteData);
			ResponseAPDU r4 = channel.transmit(updateData);//����д��ָ��
			System.out.println("WRITE response: " + bytesToHexString(r4.getBytes()));//��ӡ����ֵ
			readData(card, pwd, sector, block, keyType, 16);
			if (bytesToHexString(r4.getBytes()).equals("9000"))
				return true;
			else 
				return false;
			} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }

    public void dataError(){
    	
    }
    
    public void dataCorrect(){
    	
    }
    
}
