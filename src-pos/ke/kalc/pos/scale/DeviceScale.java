/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/


package ke.kalc.pos.scale;

import java.awt.Component;
import ke.kalc.commons.utils.TerminalInfo;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppProperties;
import ke.kalc.pos.util.StringParser;

/**
 *
 *
 */
public class DeviceScale {

    private Scale m_scale;

    /**
     * Creates a new instance of DeviceScale
     *
     * @param parent
     * @param props
     */
    public DeviceScale(Component parent, AppProperties props) {
        StringParser sd = new StringParser(TerminalInfo.getScales());
        String sScaleType = sd.nextToken(':');
        String sScaleParam1 = sd.nextToken(',');
        // String sScaleParam2 = sd.nextToken(',');
        switch (sScaleType) {
            case "Adam Equipment":
                m_scale = new ScaleAdam(sScaleParam1, parent);
                break;
            case "casiopd1":
                m_scale = new ScaleCasioPD1(sScaleParam1);
                break;
            case "dialog1":
                m_scale = new ScaleComm(sScaleParam1);
                break;
            case "samsungesp":
                m_scale = new ScaleSamsungEsp(sScaleParam1);
                break;
            case "caspdii":
                m_scale = new ScaleCASPDII(sScaleParam1);
                break;
            case "fake":
                // a fake scale for debugging purposes
                m_scale = new ScaleFake();
                break;
            case "screen":
                // on screen scale
                m_scale = new ScaleDialog(parent);
                break;
            default:
                m_scale = null;
                break;
        }
    }

    /**
     *
     * @return
     */
    public boolean existsScale() {
        return m_scale != null;
    }

    /**
     *
     * @return @throws ScaleException
     */
    public Double readWeight() throws ScaleException {

        if (m_scale == null) {
            throw new ScaleException(AppLocal.getIntString("scale.notdefined"));
        } else {
            Double result = m_scale.readWeight();
            if (result == null) {
                return null; // Canceled by the user / scale
            } else if (result.doubleValue() < 0.002) {
                // invalid result. nothing on the scale
                throw new ScaleException(AppLocal.getIntString("scale.invalidvalue"));
            } else {
                // valid result
                return result;
            }
        }
    }
}
