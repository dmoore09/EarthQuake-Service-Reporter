import realtimeweb.earthquakeservice.domain.Earthquake;
import java.util.ArrayList;
import realtimeweb.earthquakeservice.domain.History;
import realtimeweb.earthquakeservice.domain.Threshold;
import realtimeweb.earthquakeservice.exceptions.EarthquakeException;
import realtimeweb.earthquakeservice.domain.Report;
import java.io.FileNotFoundException;
import realtimeweb.earthquakewatchers.WatcherParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import realtimeweb.earthquakewatchers.WatcherService;
import realtimeweb.earthquakeservice.regular.EarthquakeService;

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.
/**
 * // -------------------------------------------------------------------------
/**
 *  Class that holds the main function. TODO better description
 *
 *  @author Dan
 *  @version Sep 4, 2013
 */
public class Main
{
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @param args array of arguments for user input
     */
    public static void main(String[] args)
    {
        //The Current Time of the simulation
        long time = 0;
        long startTime = 0;

        //initialize Linked List for watchers
        LList<String> watcherList = new LList<String>();

        //initialize Data structures to be used
        //initialize Queue
        LQueue<Earthquake> queueQuake = new LQueue<Earthquake>();

        //initialize heap
        Double[] eq = new Double[1000];
        MaxHeap<Double> maxEarthQuake = new MaxHeap<Double>(eq,  0, 1000);

      //open data from normal earthquake file
        InputStream normalEarthquakes;
        EarthquakeService earthquakeService = null;
        try
        {
            normalEarthquakes = new FileInputStream("normal.earthquakes");
            earthquakeService = EarthquakeService.getInstance(normalEarthquakes);
        }
        catch (IOException e)
        {
            System.out.print("File not found");
            e.printStackTrace();
        }

        //open data watcher data from test file
        InputStream watcherCommandFile;
        WatcherService watcherService = null;
        try
        {
            watcherCommandFile = new FileInputStream("watcher.txt");
            watcherService = WatcherService.getInstance(watcherCommandFile);
        }
        catch (FileNotFoundException e1)
        {
            System.out.print("File not found");
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.print("IOException");
            e.printStackTrace();
        }
        catch (WatcherParseException e)
        {
            System.out.print("ParseException");
            e.printStackTrace();
        }


        boolean start = false;

        //Program will continue running as long as the watcher service
        //has commands
        while(watcherService.hasCommands())
        {
            //get latest commands from service
            ArrayList<String> commands = watcherService.getNextCommands();

            //Put new commands on the LList
            for (int i = 0; i < commands.size(); i++)
            {
                //print our new command

                //add onto watcher list
                watcherList.insert(commands.get(i));


            }

           //get a report for the past hour
            Report latestQuakes = null;
            try {
                latestQuakes = earthquakeService.getEarthquakes(Threshold.ALL, History.HOUR);
            } catch (EarthquakeException e) {
                e.printStackTrace();
            }



            //first time through initialize time
            if (!start)
            {
                time = latestQuakes.getGeneratedTime();
                startTime = time;
                start = true;
            }
            else
            {
              //increment simulation time by 5 minutes
                time += (5 * 60);
            }
            ArrayList<Earthquake> newPoll = latestQuakes.getEarthquakes();
            System.out.print(time + "     ");


            //Add new EarthQuakes to Queue and Heap
            for (int i = 0; i < newPoll.size(); i++)
            {
                Earthquake current = newPoll.get(i);
                //compare time stamps of current and simulation time to
                //make sure it is new
                if (current.getTime() >= time)
                {
                    //enqueue earthquake onto the rear of quakeQueue
                    queueQuake.enqueue(current);
                    //add its magnitude value onto the heap
                    //TODO add more info about position
                    maxEarthQuake.insert(current.getMagnitude());
                    System.out.print("new quake");
                }
            }


            //remove old quakes
            while(queueQuake.length() > 0){
                if(time - 21600 > queueQuake.frontValue().getTime()) {
                    //remove front value
                    queueQuake.dequeue();
                    System.out.print("  Quake removed  ");
                    //TODO remove from heap
                }
                else {
                    break;
                }
            }

        }


    }
}

