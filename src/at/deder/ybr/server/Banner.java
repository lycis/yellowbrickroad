package at.deder.ybr.server;

import java.util.Objects;

/**
 * Provides access to the data of a banner that is configured for a server.
 * @author lycis
 */
public class Banner {
    private String bannerText = "";
    
    public Banner() {
        
    }
    
    public Banner(String text) {
        bannerText = text;
    }
    
    public void setText(String text) {
        bannerText = text;
    }
    
    public String getText() {
        return bannerText;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.bannerText);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Banner other = (Banner) obj;
        if (!Objects.equals(this.bannerText, other.bannerText)) {
            return false;
        }
        return true;
    }
    
    
}
