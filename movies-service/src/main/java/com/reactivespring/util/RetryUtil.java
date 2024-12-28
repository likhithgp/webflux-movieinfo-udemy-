package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

public class RetryUtil {

    public static Retry retrySpec(){

        return Retry.fixedDelay(3, java.time.Duration.ofSeconds(2))//3 retries, with each retry after 2 seconds
                .filter(ex->ex instanceof MoviesInfoServerException || ex instanceof ReviewsServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())
                );
    }
}
