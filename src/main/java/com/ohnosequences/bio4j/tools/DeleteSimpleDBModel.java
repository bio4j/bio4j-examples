/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ohnosequences.bio4j.tools;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class DeleteSimpleDBModel {

    public static final String BIO4J_DOMAIN = "bio4j";

    public static void main(String[] args) {
        try {
            
            AmazonSimpleDBClient simpleDBClient = new AmazonSimpleDBClient();

            System.out.println("Deleting domain (if existed)...");
            simpleDBClient.deleteDomain(new DeleteDomainRequest(BIO4J_DOMAIN));
            System.out.println("done!");
            
            
        } catch (Exception ex) {
            Logger.getLogger(DeleteSimpleDBModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
