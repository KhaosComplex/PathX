/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xpath.file;

/**
 * This enum helps the model keep track of what edit operation
 * the user is currently doing so that the UI can provide the
 * appropriate response.
 * 
 * @author Richard McKenna
 */
public enum XPathEditMode
{
    NOTHING_SELECTED,
    INTERSECTION_SELECTED,
    INTERSECTION_DRAGGED,
    ROAD_SELECTED,
    ADDING_INTERSECTION,
    ADDING_ROAD_START,
    ADDING_ROAD_END
}

