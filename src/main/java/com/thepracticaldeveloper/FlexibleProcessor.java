package com.thepracticaldeveloper;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlexibleProcessor<T, R>
  extends SubmissionPublisher<R>
  implements Flow.Processor<T, R> {

  private static final Logger log = LoggerFactory.
    getLogger(FlexibleProcessor.class);

  private final Function<T, R> function;
  private final Predicate<T> predicate;
  private Flow.Subscription subscription;

  FlexibleProcessor(final Function<T, R> function,
                    final Predicate<T> predicate) {
    super();
    this.function = function;
    this.predicate = predicate;
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void onNext(T item) {
    if (predicate.test(item)) {
      submit(function.apply(item));
    } else {
      log.info("<==========> The processor is not submitting this item: " + item);
    }
    subscription.request(1);
  }

  @Override
  public void onError(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void onComplete() {
    close();
  }
}
