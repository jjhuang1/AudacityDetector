import java.io.File
import java.io.Reader
import java.io.InputStreamReader
import java.io.BufferedReader


fun main(args:Array<String>) {
    val os = System.getProperty("os.name").toUpperCase() //current user's OS

    var path: String //path to Downloads directory

    //temporarily hard coded; currently must be a non-empty file to avoid invalid/corruption error in Audacity
    var filename = "take1.wav"

    //temporarily hard coded for Audacity
    var program = "audacity"

    //find the path to the Downloads directory
    if (os.contains("WIN")){
        path = "${System.getenv("USERPROFILE")} + /Downloads"
    }
    else if (os.contains("MAC")){
        path = System.getProperty("user.home") + "/Downloads"
    }
    else { //Linux OS
        path = System.getProperty("user.home")
    }
    println("File is in ${path}")

    //create and record the take file
    try {
        val take = File(path + "/${filename}")
        take.createNewFile() //make a new wav file for the take in Downloads
        println("Opening audacity")

        //open Audacity (must be installed on the user's computer)
        try{
            //determines and executes the appropriate command to open Audacity
            if (os.contains("WIN")){
                val process = ProcessBuilder("start", "${program}", "${path}/${filename}")
                process.start()
            }
            else if (os.contains("MAC")){
                val process = ProcessBuilder("open", "-a", "${program}", "${path}/${filename}")
                process.start()
            }
            else { //Linux OS
                val process = ProcessBuilder("${program}", "${path}/${filename}")
                process.start()
            }

            //calls helper function to get the current status of Audacity
            var status = getCurrStatus(os, true)
            while(status == 1){
                status = getCurrStatus(os, false)
            }
            println("User has closed the program")

        }
        catch (e:Exception) {
            println("Error getting status of ${program}")
            e.printStackTrace()
        }
    }
    catch (e:Exception) {
        println("Error creating file")
        e.printStackTrace()
    }
}



fun getCurrStatus(os: String, first: Boolean):Int{
    val status_file: String

    /*locates the appropriate shell executable file to execute in order to determine the
      status of Audacity on the user's system*/
    if (os.contains("WIN")) {
        status_file = "./findstatus_WIN.sh"
    }
    else if (os.contains("MAC")){
        status_file = "./findstatus_MAC.sh"
    }
    else{
        status_file = "./findstatus_LINUX.sh"
    }

    /*execute the appropriate shell executable file to find currently running
      processes on the system and filter according to Audacity*/
    val audacityPB = ProcessBuilder(status_file)
    val audacityProcess = audacityPB.start()

    //load the output of the shell executable
    val stdin = BufferedReader(InputStreamReader(audacityProcess.getInputStream()))
    var prcs = arrayOf("0", "0")

        var s = stdin.readLine()

        while(s != null){
            if (s.contains("grep Audacity.app")){
                prcs[1] = s
            }
            else {
                prcs[0] = s
            }
            s = stdin.readLine()
        }
        
        if (os.contains("MAC")){
            if (!first){
                //if Audacity is not a currently running process
                if (prcs[0].equals("0")){
                    return 0
                }
            }
            return 1
        }
        else{
            //if Audacity is not a currently running process
            if (prcs[0].equals("0")){
                return 0
            }
        }
        return 1
}





