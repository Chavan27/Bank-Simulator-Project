package PJ3;

class Customer
{
    private int customerID;
    private int transactionTime;
    private int arrivalTime;
    private int finishTime;
    private int waitTime;
    Customer()
    {
        customerID=0;
        transactionTime=0;
        arrivalTime=0;
        finishTime=0;
        waitTime=0;


    }
    
    Customer(int customerid, int transactionduration, int arrivaltime)
    {
        customerID = customerid;
        transactionTime = transactionduration;
        arrivalTime = arrivaltime;
       
    }
       void setFinishTime(int finishtime) 
    {
  	finishTime=finishtime; 
    }
       
         int getFinishTime() 
    {
  	return finishTime; 
    }

    int getTransactionTime()
    {
        return transactionTime;
    }
    
    int getArrivalTime()
    {
        return arrivalTime;
    }
    
    int getCustomerID()
    {
        return customerID;
    }
    
       int getWaitTime() 
    {
  	return waitTime; 
    }

    void setWaitTime(int time) 
    {
  	waitTime=time; 
    }
    
    public String toString()
    {
        return ""+customerID+":"+transactionTime+":"+arrivalTime;
    }
    
    public static void main(String[] args)
    {
        // quick check!
        Customer mycustomer = new Customer(1,5,18);
        mycustomer.setFinishTime(28);
        System.out.println("Customer Info:"+mycustomer);
    }
    
}
