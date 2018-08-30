/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Math;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N) {
        
        List<MaThread> rango = new ArrayList();
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        AtomicInteger ocurrencesCount= new AtomicInteger(0);
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int cantidad= skds.getRegisteredServersCount();
        
        int divid = cantidad/N;
        int checkedListsCount=0;
        
        for(int i=0; i <N;i++){
            System.out.print("inicio"+i*divid+" fin "+divid*(i+1));
            MaThread hilo = new MaThread(ipaddress,i*divid,divid*(i+1),cantidad);
            rango.add(hilo);
            hilo.start();
            
        }
        
        for(MaThread j: rango){
            try {
                j.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(HostBlackListsValidator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for(MaThread b: rango){
            blackListOcurrences.addAll(b.getBlackListOcur());
            checkedListsCount +=b.getOcurrencesCount().get();
        }
        
        
        
        /*
        los hilos hacen lo del for Gg  
        */
        
        if (blackListOcurrences.size()>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}