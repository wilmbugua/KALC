/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.data.loader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import ke.kalc.basic.BasicException;
import ke.kalc.pos.forms.AppLocal;


public class BatchSentenceResource extends BatchSentence {

    private String m_sResScript;
    
    /** Creates a new instance of BatchSentenceResource
     * @param s
     * @param resscript */
    public BatchSentenceResource(Session s, String resscript) {
        super(s);
        m_sResScript = resscript;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    protected Reader getReader() throws BasicException {
        
        InputStream in = BatchSentenceResource.class.getResourceAsStream(m_sResScript);
        
        if (in == null) {
            throw new BasicException(AppLocal.getIntString("exception.nosentencesfile"));
        } else {  
            try {
                return new InputStreamReader(in, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new BasicException(AppLocal.getIntString("exception.nosentencesfile"), ex);
            }
        }
    }   
}
