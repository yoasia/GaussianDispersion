/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import java.util.Comparator;
import org.json.simple.JSONObject;

/**
 *
 * @author joanna
 */
public class ResultComparator  implements Comparator<JSONObject> {
    /*
    * (non-Javadoc)
    * 
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * lhs- 1st message in the form of json object. rhs- 2nd message in the form
    * of json object.
    */
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        return (double)lhs.get("value") > (double)rhs.get("value") ? 1 : (
                    (double)lhs.get("value") < (double)rhs.get("value") ? -1 : 0);
    }
}