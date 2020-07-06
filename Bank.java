import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
//-----------------------------------------------------------------------------------------------------------BANK----------------------------------------------------------------
public class Bank {
    public static void main (String[] args) {
        String fIn  = "Bank.dat.txt";
        //C:\Users\Adrian\IdeaProjects\Project2_BankSystem\src
        String fErr = "Bank.err.txt";
        
        Map<String,Account> accs = readData(fIn,fErr);

        for (Map.Entry<String,Account> e : accs.entrySet())
            System.out.println(e.getValue());

        try {
            String errLog = Files.readString(Paths.get(fErr));
            System.out.println("\nContent of " +
                    "\"Bank.err\" follows:\n");
            System.out.println(errLog);
        } catch(IOException e) {
            System.out.println("Problems with error log");
            return;
        }
    }

    public static Map<String, Account> readData(String a, String b){


        FileWriter fw = null;
        BufferedReader br;
        String currentLine;
        Map<String ,Account> accs = new HashMap<>();
        try{
            br = new BufferedReader(new FileReader(a));
            File error_f = new File(b);
            error_f.createNewFile();
            fw = new FileWriter(b, true);




            //доделать аутпут!



            // first case
            Pattern p1 = Pattern.compile("^[A-Z][a-z]{1,20}([A-Z])\\s[A-Z][a-z]{1,20}\\s\\1[a-z]{1,20}\\s[1-9]\\d*$");

            // second case
            Pattern p2_1 = Pattern.compile("^[A-Z][a-z]{1,20}[A-Z]\\s-[1-9]\\d*$");
            Pattern p2_2 = Pattern.compile("^[A-Z][a-z]{1,20}[A-Z]\\s[1-9]\\d*$");

            // third case
            Pattern p3 = Pattern.compile("[A-Z][a-z]{1,20}[A-Z]\\s[A-Z][a-z]{1,20}[A-Z]\\s\\d*$");



            int count = 1;
            while((currentLine = br.readLine()) != null){
                Matcher m1 = p1.matcher(currentLine);
                Matcher m2_1 = p2_1.matcher(currentLine);
                Matcher m2_2 = p2_2.matcher(currentLine);
                Matcher m3 = p3.matcher(currentLine);

                String[] arr_tmp = currentLine.split(" ");


                if(arr_tmp.length==4 && m1.matches()) {
                    if (accs.containsKey(arr_tmp[0])) {
                        //error1
                        String error1 ="Line \t" +  count + currentLine + "\n \tError: Unique ID error\n";
                        fw.append(error1);
                    } else {
                        accs.put(arr_tmp[0], new Account(arr_tmp[0], new Person(arr_tmp[1], arr_tmp[2]), Integer.parseInt(arr_tmp[3])));
                    }
                }
                else if(arr_tmp.length==2 && m2_1.matches()){
                    if(accs.get(arr_tmp[0]).getBalance()<Math.abs(Integer.parseInt(arr_tmp[1]))){
                        //error2
                        String error2 = "Line \t" + count + " : " + currentLine + "\n \tError: Wrong amount\n";
                        fw.append(error2);
                    } else{
                        int amount = Integer.parseInt(arr_tmp[1]);
                        accs.get(arr_tmp[0]).incDec(amount);
                    }
                }
                else if(arr_tmp.length==2 && m2_2.matches()){

                        int amount = Integer.parseInt(arr_tmp[1]);
                        accs.get(arr_tmp[0]).incDec(amount);
                }
                else if(arr_tmp.length==3 && m3.matches()){
                    if (!accs.containsKey(arr_tmp[0]) || !accs.containsKey(arr_tmp[1])){
                        //error3.1
                        String error3 = "Line \t" + count + " : " + currentLine + "\n \tError: Nonexistent account is mentioned\n";
                        fw.append(error3);


                    }else {
                        if ((accs.get(arr_tmp[0]).getBalance()<Integer.parseInt(arr_tmp[2]))){
                            //error3
                            String error3 = "Line \t" + count + " : " + currentLine + "\n \tError: Insufficient funds\n";
                            fw.append(error3);
                        } else
                            accs.get(arr_tmp[0]).transTo(accs.get(arr_tmp[1]), Integer.parseInt(arr_tmp[2]));
                    }
                }
                else{
                    if (accs.containsKey(arr_tmp[0])) {
                        //error1
                        System.out.println();
                        String error1 = "Line \t" + count + " : " + currentLine + "\n \tError: Unique ID error\n";
                        fw.append(error1);
                    } else {
                        //error4(final)
                        String error_final = "Line \t" + count  + " : " + currentLine + "\n \tError: Entry inappropriate format \n";
                        fw.append(error_final);
                    }
                }
                count++;
            }
        }catch(IOException e){
            e.printStackTrace();
        } finally {
            try {
         /*???*/assert fw != null;
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return accs;

    }

}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//-----------------------------------------------------------------------------------------------------------Person----------------------------------------------------------------
class Person{
    private String fname,  lname;

    Person(String fname, String lname){
        this.fname=fname;
        this.lname=lname;
    }

    @Override
    public String toString(){
        return fname + " " + lname ;
    }


}
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------Account----------------------------------------------------------------
class Account{
    private String id;
    private Person owner;
    private int balance;
    private List<Transaction> list;



    String[] opTypes =
            {
                    "Init depos ", "Deposit ", "Withdrawal ", "Trnas. from ", "Trans. to "
            };

    Account(String id, Person owner, int balance){
        this.id=id;
        this.owner=owner;
        this.balance=balance;
        list = new ArrayList<>();
        list.add(new Transaction("+" + balance + " : " + opTypes[TransactionType.INIT_DEPOS.ordinal()] +"\n"));
    }

    public void transTo(Account a, int amount){
        long trans_id = Transaction.getNextId();
        String inList_my = -amount + " : " + opTypes[TransactionType.TRANS_FORM.ordinal()] + "this account to " + a.getPerson() + " (" +  a.getId() + ")\n";
        String inList_foreign = "+" + amount + " : " + opTypes[TransactionType.TRANS_TO.ordinal()] + "this account from " + getPerson() + " (" +  getId() + ")\n";
        list.add(new Transaction(inList_my, trans_id));
        a.list.add(new Transaction(inList_foreign, trans_id));
        a.balance+=amount;
        this.balance-=amount;
    }


    public void incDec(int amount){
        balance+=amount;
        if(amount>0)
        list.add(new Transaction("+" + amount + " : " + opTypes[TransactionType.DEPOSIT.ordinal()] + "\n"));
        if(amount<0)list.add(new Transaction(amount + " : " + opTypes[TransactionType.WITHDRAWAL.ordinal()] + "\n"));

    }


    public Person getPerson(){
        return owner;
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public String toString(){
        StringBuilder out = new StringBuilder("*** Acc " + id + "(" + owner + ") " + ". Balance: "+ balance + ". Transactions:  \n");
        for (Transaction t: list
             ) {
            out.append(String.format( t.toString(), "%-10d"));
        }
        return out.toString();
    }

}
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------Transaction----------------------------------------------------------------
class Transaction{
    private long id;
    private String details;
    private static long counter = 3129398;

    Transaction(String details){
        id = counter;
        counter++;
        this.details = details;
    }

    Transaction(String details, long id){
        this.id = id;
        this.details = details;

    }

    public long getId() {
        return id;
    }

    public static long getNextId(){
        counter++;
        long ret = counter;
        counter++;
        return ret;
    }


    @Override
    public String toString(){
        return "trans_num " + id + " | "+ details;
    }
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------Enum:TransactionType----------------------------------------------------------------
enum TransactionType{
    INIT_DEPOS, DEPOSIT, WITHDRAWAL, TRANS_FORM, TRANS_TO
}
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
