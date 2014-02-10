/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.simpledb;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.era7.bioinfo.bioinfoaws.util.CredentialsRetriever;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class DeleteSimpleDBModel implements Executable{

    public static final String BIO4J_DOMAIN = "bio4j";
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {
        try {
            
            AmazonSimpleDBClient simpleDBClient = new AmazonSimpleDBClient(CredentialsRetriever.getBasicAWSCredentialsFromOurAMI());

            System.out.println("Deleting domain (if existed)...");
            simpleDBClient.deleteDomain(new DeleteDomainRequest(BIO4J_DOMAIN));
            System.out.println("done!");
            
            
        } catch (Exception ex) {
            Logger.getLogger(DeleteSimpleDBModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
