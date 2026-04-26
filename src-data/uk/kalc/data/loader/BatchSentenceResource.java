/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
**
*/


package uk.kalc.data.loader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import uk.kalc.basic.BasicException;
import uk.kalc.pos.forms.AppLocal;


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
