/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callables;

import java.util.concurrent.Callable;
import utils.Utility;
import utils.api.MakeOptions;

/**
 *
 * @author christianrosenbaek
 */
public class ApiFetchCallable implements Callable<String>{
    private String url;
    private MakeOptions makeOptions;

    public ApiFetchCallable(String url, MakeOptions makeOptions) {
        this.url = url;
        this.makeOptions = makeOptions;
    }
    
    @Override
    public String call() throws Exception {
        return Utility.fetchData(url, makeOptions);
    }
    
}
