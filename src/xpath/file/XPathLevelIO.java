/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xpath.file;

import java.io.File;
import xpath.file.XPathLevel;
import xpath.file.XPathModel;

/**
 *
 * @author McKillaGorilla
 */
public interface XPathLevelIO
{
    public boolean loadLevel(File levelFile, XPathModel model);
}
