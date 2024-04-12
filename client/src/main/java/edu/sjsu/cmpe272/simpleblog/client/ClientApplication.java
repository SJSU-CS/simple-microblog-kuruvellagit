package edu.sjsu.cmpe272.simpleblog.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;

import edu.sjsu.cmpe272.simpleblog.client.model.MessagerRequest;
import edu.sjsu.cmpe272.simpleblog.client.repository.MessageRepository;
import edu.sjsu.cmpe272.simpleblog.client.service.MessageService;

@SpringBootApplication
@SpringBootConfiguration
@ComponentScan(basePackages = {"edu.sjsu.cmpe272.simpleblog", "edu.sjsu.cmpe272.simpleblog.client"})
//@ComponentScan("edu.sjsu.cmpe272.simpleblog.client")
public class ClientApplication implements CommandLineRunner  {
	@Autowired
	private MessageService messageService;
	@Autowired
	private MessageRepository messageRepository;
	
	public static void main(String[] args) {
		
		SpringApplication.run(ClientApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		 if (args.length == 0) {
	            System.out.println("No Usage of springboot cli commands");
	            return;
	        }

	        String command = args[0];
	        
	        
	        
	        
	        String[] arguments = Arrays.copyOfRange(args, 1, args.length);
	        boolean createuser=false;
	        switch (command) {
	            case "post":
               String message = arguments[1];
                System.out.println("message......... "+message);
                String attachment = null;
                MessagerRequest messagerRequest=new MessagerRequest();
                
                if (args.length > 2) {
                    String fileToAttach = arguments[2];
                    
                    String datetime=dateTime();

                   
                    messagerRequest.setDate(datetime);
                    messagerRequest.setAuthor("ben");
                    messagerRequest.setMessage(message);

//	
                        if (fileToAttach != null && !fileToAttach.isEmpty()) {
                            try {
                            	 byte[] fileBytes = Files.readAllBytes(Paths.get(fileToAttach));
 	                            attachment = Base64.getEncoder().encodeToString(fileBytes);
 	                           messagerRequest.setAttachment(attachment);
 		                        messagerRequest.setSignature(messageService.signMessage(datetime, "ben", message, attachment));
 		                       String result=messageService.createMessage(messagerRequest);
 			       		        System.out.println("Response ---   "+result);
 			            	
                            } catch (IOException e) {
                                System.err.println("Error reading file: " + e.getMessage());
                            }
                        }
                        else {
                        	messagerRequest.setAttachment("");
                        	messagerRequest.setSignature("");
                        	 //String result=messageService.createMessage(messagerRequest);
 			       		        System.out.println("Response--- Data are not added in database file is not given");
                        }
                        break;
                           
	                    }
                else {
                	System.out.println("File not attach....");
                	break;
                }
	                
	                
	                
	                
	            case "list":
	            	String startingId = null;
	                int count = 10;
	                boolean saveAttachment = false;

	                // Parse command line options
	                for (int i = 1; i < args.length; i++) {
	                    if (args[i].equals("--starting") && i + 1 < args.length) {
	                        startingId = args[i + 1];
	                        i++; // Skip next argument (starting ID)
	                    } else if (args[i].equals("--count") && i + 1 < args.length) {
	                        count = Integer.parseInt(args[i + 1]);
	                        i++; // Skip next argument (count)
	                    } else if (args[i].equals("--save-attachment")) {
	                        saveAttachment = true;
	                    }
	                }
	            	
	            	
	                messageService.allMessage( startingId, count, saveAttachment);
	            	
	            	
	                 break;
	            	
	            case "createUser":
	               ///messageService.createId() ;
	            	String user=arguments[0];
	            	
	            	if(createuser==false) {
	            		String pvtkey=messageService.createUsersavefile(user);
		                System.out.println("Private key  result= "+pvtkey);
		                System.out.println("Success Respons-----");
		                System.out.println("User save successfully in mb.ini file..");
		                createuser=true;
	            	}
	            	else {
	            		System.out.println("Error Response ----");
	            		System.out.println("Oh! User does not have perssion to create mb.ini file");
	            	}
	            	//System.out.println("id "+id);
//	            	String pvtkey=messageService.createUser(user);
//	                System.out.println("Private key  result= "+pvtkey);
	            	break;
	            	
	            default:
	                System.out.println("Unknown command: " + command);
	        }
		
	}
	public String dateTime()
	{
		LocalDateTime localDateTime = LocalDateTime.now();

        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        // Define date time formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");

        // Format the ZonedDateTime object to a string
        String formattedDate = zonedDateTime.format(formatter);
        return formattedDate;
	}

}
