package PJ3;

import java.util.*;
import java.io.*;

class BankSimulator
{
    // input parameters
    private int numTellers, customerQLimit;
    private int simulationTime, dataSource;
    private int chancesOfArrival, maxTransactionTime;
    
    // statistical data
    private int numGoaway, numServed, totalWaitingTime;
    
    // internal data
    private int counter;	
    private int customerIDCounter;
    private ServiceArea servicearea; // service area object
    private Scanner dataFile;        // get customer data from file
    private Random dataRandom;       // get customer data using random function
    
    // most recent customer arrival info, see getCustomerData()
    private boolean anyNewArrival;
    private int transactionTime;
    
    // initialize data fields
    private BankSimulator()
    {
      	numTellers=0;
        customerQLimit=0;
        chancesOfArrival=0;
        maxTransactionTime=0;
        simulationTime=0;
        numGoaway=0;
        numServed=0;
        totalWaitingTime=0;
        counter=1;
        dataFile = new Scanner(System.in);
        anyNewArrival=false;
        transactionTime=0; 
    }
    
    private void setupParameters()
    {
        // read input parameters
        // setup dataFile or dataRandom
        
        Scanner input = new Scanner(System.in);
        System.out.println("\n\t***  Get Simulation Parameters  ***\n");
        
        do {
            System.out.print("Enter simulation time (max is 10000): ");
            simulationTime = input.nextInt();
        } while (simulationTime > 10000 || simulationTime < 0);
        do {
            System.out.print("Enter maximum transaction time of customers (max is 500): ");
            maxTransactionTime = input.nextInt();
        } while (maxTransactionTime > 500 || maxTransactionTime < 0);
        do {
            System.out.print("Enter chances (0% < & <= 100%) of new customer: ");
            chancesOfArrival = input.nextInt();
        } while (chancesOfArrival > 100 || chancesOfArrival <= 0);
        do {
            System.out.print("Enter the number of tellers (max is 10): ");
            numTellers = input.nextInt();
        } while (numTellers > 10 || numTellers < 0);
        do {
            System.out.print("Enter customer queue limit (max is 50): ");
            customerQLimit = input.nextInt();
        } while (customerQLimit > 50 || customerQLimit < 0);
        do {
            System.out.print("Enter 1/0 to get data from file/Random: ");
            dataSource = input.nextInt();
        } while (dataSource > 1 || dataSource < 0);
        
        if (dataSource == 1) {
            System.out.print("Reading data from file. Enter file name: ");
            try {
                dataFile = new Scanner( new File(input.next()) );
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. Randomizing data instead.");
                dataSource = 0;
            }
        } else {
            System.out.println("Randomizing data.");
        }
        
        input.close();
        dataRandom = new Random();
    }
    
    private void getCustomerData()
    {
        // get next customer data : from file or random number generator
        // set anyNewArrival and transactionTime
        
        if (dataSource == 1) {
            int data1, data2;
            data1 = data2 = 0;
            
            // assign 2 integers from file to data1 & data2
            if (dataFile.hasNextInt()) {
                data1 = dataFile.nextInt();
                data2 = dataFile.nextInt();
            }
            
            anyNewArrival = (((data1%100)+1) <= chancesOfArrival);
           
            transactionTime = (data2%maxTransactionTime)+1;
            
        } else {
            anyNewArrival = ((dataRandom.nextInt(100)+1) <= chancesOfArrival);
            transactionTime = dataRandom.nextInt(maxTransactionTime)+1;
        }
    }
    
    private void doSimulation()
    {
       System.out.println();
        System.out.println("******* START SIMULATION ********");
        System.out.println();
        System.out.println("Customer #1 to #"+counter+" is ready.....");
	// Initialize CheckoutArea
        servicearea=new ServiceArea(numTellers, customerQLimit);

	// Time driver simulation loop
  	for (int currentTime = 0; currentTime < simulationTime; currentTime++) {
            
            

    		// Step 1: any new customer enters the checkout area?
    		getCustomerData();
                                
    		if (anyNewArrival) 
                {

      		    // Step 1.1: setup customer data
                    Customer customer=new Customer(counter, transactionTime,currentTime);
                    
                    
      		    // Step 1.2: check customer waiting queue too long?
                        if(!servicearea.isCustomerQTooLong())
                        {
                            servicearea.insertCustomerQ(customer);
                            System.out.println("\tcustomer#"+counter+" arrives with checkout time "+transactionTime+ " units");
                            System.out.println("\tcustomer #"+counter+" wait in customer queue");
                        }
                        
                        else
                        {
                            System.out.println("customer Queue is full");
                        }
		    //           if customer queue is too long, update numGoaway
		    //           else goto customer queue
    		} else {
      		    System.out.println("\tNo new customer!");
    		}

    		// Step 2: free busy cashiers that are done at currenttime, add to free cashierQ
                freeBusyTellers(servicearea, currentTime);
                
    		// Step 3: get free cashiers to serve waiting customers at currenttime
                if(!servicearea.emptyFreeTellerQ() && !servicearea.emptyCustomerQ())
                {
                    setFreeTellersToBusy(servicearea, currentTime);
                    counter++;
                }
                
  	} // end simulation loop
    }
    
    private void printStatistics()
    {
       // add statements into this method!
	// print out simulation results
        System.out.println();
        System.out.println("*******************End of Simulation report***************** ");
        System.out.println();
        
        
	// see the given example in project statement
        // you need to display all free and busy gas pumps
        
        System.out.println("\t# of arrival customers    : " + (counter + numGoaway -1));
        System.out.println("\t# customer gone-away      : " + numGoaway);
        System.out.println("\t# customers served        : " + (counter - 1));

        System.out.println("\n\t*** Current Cashiers Info ***\n");
        System.out.println("\t# WaitingCustomers        : " + servicearea.numWaitingCustomers());
        System.out.println("\t# Busy cashiers           : " + servicearea.numBusyTellers());
        System.out.println("\t# Free cashiers           : " + servicearea.numFreeTellers());
        // need to free up all customers in queue to get extra waiting time.
        System.out.println("\n\tTotal waiting time       : "+totalWaitingTime);
        System.out.println("\tAverage waiting time      : "+totalWaitingTime/simulationTime);

        // need to free up all cashiers in queue to get extra free & busy time.
        System.out.println("\n\n\tBusy Cashiers Info:");
        
        while(!servicearea.emptyBusyTellerQ())
        {
          Teller busyCashier = servicearea.removeBusyTellerQ();
          busyCashier.printStatistics();
        }

        System.out.println("\n\nFree Cashiers Info: ");
        
        while(!servicearea.emptyFreeTellerQ())
        {
        Teller freeCashier = servicearea.removeFreeTellerQ();
        freeCashier.printStatistics();
        }
   
        
    }
    
        
    private void freeBusyTellers(ServiceArea servicearea, int currentTime)
    {
        while(!servicearea.emptyBusyTellerQ())
        {
          //Peek at next cashier's customer to see if their service time is now done
          Teller busyTeller = servicearea.getFrontBusyTellerQ();
          int busyCustomerEndServiceTime = getBusyCashiersCustomerEndServiceTime(busyTeller);

          if(busyCustomerEndServiceTime <= currentTime)
          {
            //If busy cashier is done serving customer, remove cashier from Queue
            busyTeller = servicearea.removeBusyTellerQ();
            System.out.println("\tCashier #" + busyTeller.getTellerID() + " is free");

            //Update Cashier's stats by changing availability as free
            Customer customer = busyTeller.busyToFree();
            System.out.println("\tCustomer #" + customer.getCustomerID() + " is done");

            //Place the now free cashier in the freeCashierQ
            servicearea.insertFreeTellerQ(busyTeller);
          }
          else
          {
            //If top cashier is not free (priority queue), end check
            break;
          }
        } 
    }
    
    private int getBusyCashiersCustomerEndServiceTime(Teller cashier)
    {
        Customer busyCustomer=cashier.getCustomer();
        int arriveTime=busyCustomer.getArrivalTime();
        int serviceTime=busyCustomer.getTransactionTime();
        return (arriveTime+serviceTime);
    }
    
    private void setFreeTellersToBusy(ServiceArea checkoutarea, int currentTime)
    {
        System.out.println("\tcustomer#"+counter+"  gets a teller");
        
        Teller teller=servicearea.removeFreeTellerQ();
        
        Customer customer=checkoutarea.removeCustomerQ();
        
        teller.freeToBusy(customer, currentTime);
        
        teller.setEndBusyTime(currentTime +transactionTime);
        
        checkoutarea.insertBusyTellerQ(teller);
        
        System.out.println("\tTeller#"+teller.getTellerID()+" start serving customer #"+counter +" for "+ transactionTime + " units");
    }
    
    private void printCurrentTimeInterval(int cTime)
    {
        System.out.println("-----------------------------------------------------------");
        System.out.println("Time :" + cTime);
    }
    
    // *** main method to run simulation ***
    
    public static void main(String[] args)
    {
        BankSimulator runBankSimulator=new BankSimulator();
        runBankSimulator.setupParameters();
        runBankSimulator.doSimulation();
        runBankSimulator.printStatistics();
    }
    
}
