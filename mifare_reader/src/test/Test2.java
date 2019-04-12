package test;

import java.util.List;

import javax.smartcardio.*;

public class Test2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TerminalFactory factory = TerminalFactory.getDefault();//�õ�һ��Ĭ�ϵĶ���������(�ԡ���)
        List<CardTerminal> terminals;//����һ��List�����Ŷ�����(˭û�»��ڵ����ϲ����ĸ�����������)
        
        try {
			terminals = factory.terminals().list();//�ӹ�����ò��ڵ����ϵĶ������б�
			//terminals.stream().forEach(s->System.out.println(s));//��ӡ��ȡ���Ķ���������
			System.out.println(terminals.toString());

			terminals = factory.terminals().list();//get�������б�
			CardTerminal a = terminals.get(0);//ʹ�õ�0��������[���Ҳ�����ͬʱ��N���������������]
			if (a.isCardPresent()){
				System.out.println("card present");
			}
			else if (!a.isCardPresent())
				System.out.println("card absent");
			System.out.println(a.isCardPresent());
			System.out.println();
			
			
			Thread t = new Thread(new Test1(a));
			t.start();
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
