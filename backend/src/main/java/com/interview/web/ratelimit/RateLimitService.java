package com.interview.web.ratelimit;

import com.interview.util.SpringBeans;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class RateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);

    private final Map<String, BucketConfiguration> bucketConfigurationsByKey = new HashMap<>();

    @EventListener(classes = { ApplicationStartedEvent.class })
    public void handleApplicatioStartedEvent(final ApplicationStartedEvent applicationStartedEvent) {
        Stream
            .of(applicationStartedEvent.getApplicationContext().getBeanNamesForAnnotation(RestController.class))
            .forEach(beanName -> {
                logger.trace("Processing bean: {}", beanName);
                final var bean = SpringBeans.getBeanWithoutProxy(applicationStartedEvent.getApplicationContext(),
                                                                 beanName,
                                                                 Object.class);
                Stream.of(bean.getClass().getMethods()).forEach(method -> {
                    final var optionalRateLimit = Optional
                        .ofNullable(AnnotationUtils.findAnnotation(method, RateLimits.class))
                        .or(() -> Optional
                            .ofNullable(AnnotationUtils.findAnnotation(bean.getClass(), RateLimits.class)));
                    final var rateLimits = optionalRateLimit
                        .orElseGet(this::defaultRateLimiters);
                    final var key = optionalRateLimit.isPresent()
                        ? "%s#%s".formatted(bean.getClass().getName(), method.getName())
                        : "default";

                    this.bucketConfigurationsByKey.computeIfAbsent(key, it -> {
                        logger.trace("Registering rate limit: {}", it);
                        return createBucketConfiguration(rateLimits);
                    });
                });
            });
        logger.debug("Rate limits configurations: {}", bucketConfigurationsByKey);
    }

    public BucketConfiguration resolveBucketConfiguration(final String key) {
        return this.bucketConfigurationsByKey.get(key);
    }

    public boolean existsBucketConfiguration(final String key) {
        return this.bucketConfigurationsByKey.containsKey(key);
    }

    private BucketConfiguration createBucketConfiguration(final RateLimits rateLimits) {
        final var builder = BucketConfiguration.builder();
        Stream
            .of(rateLimits.value())
            .map(this::createBandwidth)
            .forEach(builder::addLimit);
        return builder.build();
    }

    private Bandwidth createBandwidth(final RateLimit rateLimit) {
        return Bandwidth.builder()
            .capacity(rateLimit.capacity())
            .refillIntervally(rateLimit.capacity(), Duration.of(rateLimit.timeValue(), rateLimit.timeUnit().toChronoUnit()))
            .build();
    }

    private RateLimits defaultRateLimiters() {
        return new RateLimits() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Annotation.class;
            }

            @Override
            public RateLimit[] value() {
                return new RateLimit[] { businessRateLimit(), burstProtectionRateLimit() };
            }
        };
    }

    private RateLimit businessRateLimit() {
        return new RateLimit() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Annotation.class;
            }

            @Override
            public long capacity() {
                return 1000;
            }

            @Override
            public long timeValue() {
                return 5;
            }

            @Override
            public TimeUnit timeUnit() {
                return TimeUnit.MINUTES;
            }
        };
    }

    private RateLimit burstProtectionRateLimit() {
        return new RateLimit() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Annotation.class;
            }

            @Override
            public long capacity() {
                return 500;
            }

            @Override
            public long timeValue() {
                return 1;
            }

            @Override
            public TimeUnit timeUnit() {
                return TimeUnit.MINUTES;
            }
        };
    }
}