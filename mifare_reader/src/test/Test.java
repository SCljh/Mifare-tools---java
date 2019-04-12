package test;

import javax.smartcardio.*;
import java.util.*;

public class Test {

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
            //sb.append("0x");
            sb.append(HEX_CHAR[a / 16]);
            sb.append(HEX_CHAR[a % 16]);
            //sb.append(" ");
        }
        return sb.toString().toUpperCase();
    }


    public static void main(String[] args) {


        TerminalFactory factory = TerminalFactory.getDefault();//�õ�һ��Ĭ�ϵĶ���������(�ԡ���)
        List<CardTerminal> terminals;//����һ��List�����Ŷ�����(˭û�»��ڵ����ϲ����ĸ�����������)
        try {
            terminals = factory.terminals().list();//�ӹ�����ò��ڵ����ϵĶ������б�
            //terminals.stream().forEach(s->System.out.println(s));//��ӡ��ȡ���Ķ���������
            System.out.println(terminals.toString());

            terminals = factory.terminals().list();//get�������б�
            CardTerminal a = terminals.get(0);//ʹ�õ�0��������[���Ҳ�����ͬʱ��N���������������]
            a.waitForCardPresent(0L);//�ȴ����ÿ�Ƭ
            Card card = a.connect("T=1");//���ӿ�Ƭ��Э��T=1 ���д(T=0ò�Ʋ�֧�֣�һ�þͱ���)
            CardChannel channel = card.getBasicChannel();//��ͨ��
            CommandAPDU getUID = new CommandAPDU(0xFF,  0xCA, 0x00, 0x00,0x04);//����API��12ҳ
            ResponseAPDU r = channel.transmit(getUID);//����getUIDָ��
            System.out.println("UID: " + bytesToHexString(r.getData()));

            byte[] pwd = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};//����һ���������Կ������
            //byte[] pwd = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};//����һ���������Կ������// ~
            CommandAPDU loadPWD = new CommandAPDU(0xFF, 0x82, 0x00, 0x00, pwd);//Ȼ����һ��������ԿAPDUָ��~
            ResponseAPDU r1 = channel.transmit(loadPWD);//����loadPWDָ��
            System.out.println("LOAD PWD result: " + bytesToHexString(r1.getBytes()));

            byte[] check = {(byte)0x01,(byte)0x00,(byte)0x05,(byte)0x60,(byte)0x00};//��֤�����ֽڣ���������Ҫ��֤������š���Կ���ͺ���Կ�洢�ĵ�ַ(��Կ��)
            CommandAPDU authPWD = new CommandAPDU(0xFF, 0x86, 0x00, 0x00, check);//����ָ��ͷ�����������������֤APDUָ��.
            ResponseAPDU r2 = channel.transmit(authPWD);//������ָ֤��
            System.out.println("CHECK PWD result: " + bytesToHexString(r2.getBytes()));//��ӡ����ֵ

            CommandAPDU getData = new CommandAPDU(0xFF, 0xB0, 0x00, 0x39,0x01);//���������APDUָ��,���ڰ˸�����(2����0����)ֵ
            ResponseAPDU r3 = channel.transmit(getData);//���Ͷ�����ָ��
            System.out.println("READ data: " + bytesToHexString(r3.getBytes()));//��ӡ����ֵ

            byte[] up = {(byte)0x1f,(byte)0x10,(byte)0xC5,(byte)0xE9,(byte)0x6B,(byte)0x0A,(byte)0x11,(byte)0x11,(byte)0xE9,(byte)0x6B,(byte)0x0A,(byte)0x11,(byte)0x11,(byte)0xE9,(byte)0x6B,(byte)0x0A};
            CommandAPDU updateData = new CommandAPDU(0xFF, 0xD6, 0x00, 0x39, up);
            ResponseAPDU r4 = channel.transmit(updateData);//����д��ָ��
            System.out.println("WRITE response: " + bytesToHexString(r4.getBytes()));//��ӡ����ֵ

            CommandAPDU getData1 = new CommandAPDU(0xFF, 0xB0, 0x00, 0x39,0x10);//���������APDUָ��,���ڰ˸�����(2����0����)ֵ
            ResponseAPDU r5 = channel.transmit(getData1);//���Ͷ�����ָ��
            System.out.println("READ data: " + bytesToHexString(r5.getBytes()));//��ӡ����ֵ
            
//            byte[] com = {(byte)0xFF, (byte)0x00, (byte)0x40, (byte)0x9A, (byte)0x04, (byte)0x08, (byte)0x06, (byte)0x2, (byte)0x01};
//            CommandAPDU getData6 = new CommandAPDU(com);
//            ResponseAPDU r6 = channel.transmit(getData6);
//            System.out.println("READ data: " + bytesToHexString(r6.getBytes()));//��ӡ����ֵ
            
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}