/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author ederda
 */
public class YbrOptionParser extends PosixParser{
    public YbrOptionParser() {
        super();
    }
    
    @Override
    protected void burstToken(String token, boolean stopAtNonOption) {
        super.burstToken(token, true);
    }
    
    @Override
    protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) {
        return super.flatten(options, arguments, true);
    }
}
