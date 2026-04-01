package ar.com.strellis.ampflower2.data.dao;

import io.reactivex.rxjava3.core.Maybe;

public interface EntityDao<T>
{
    Maybe<T> listEntitiesObservable(String entityId);
}
