/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 2108310
 */
public class MaThread extends Thread{
    int inicio;
    int fin;
    LinkedList<Integer> blackListOcur = new LinkedList();
    String ippdres;
    int rango;
    AtomicInteger ocurrencesCount= new AtomicInteger(0);
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    
    public MaThread(String ipaddress, int I,int F,int n ){
        ippdres=ipaddress;
        inicio=I;
        fin=F;
        rango=n;

    }
    
    @Override
    public void run(){
        int i=inicio;
         while (blackListOcur.size()<5 && i<fin ){
            ocurrencesCount.incrementAndGet();
            if (skds.isInBlackListServer(i, ippdres)){
                
                blackListOcur.add(i);

            }i++;
        }
    }

    public AtomicInteger getOcurrencesCount() {
        return ocurrencesCount;
    }

    public void setOcurrencesCount(AtomicInteger ocurrencesCount) {
        this.ocurrencesCount = ocurrencesCount;
    }

    public LinkedList<Integer> getBlackListOcur() {
        return blackListOcur;
    }

    public void setBlackListOcur(LinkedList<Integer> blackListOcur) {
        this.blackListOcur = blackListOcur;
    }
}
