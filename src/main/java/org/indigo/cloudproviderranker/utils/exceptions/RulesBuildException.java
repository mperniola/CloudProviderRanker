package org.indigo.cloudproviderranker.utils.exceptions;

import org.kie.api.builder.Results;

// TODO docs

/**
 * Doc TODO.
 */
public class RulesBuildException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Results results = null;

    /**
     * Doc TODO.
     */
    public RulesBuildException(final Results results) {
        super("Rules Build Errors:\n" + results);
        this.results = results;
    }

    /**
     * 
     * @return
     */
    public Results getResults() {
        return results;
    }
}