package ar.com.strellis.ampflower.data.dao;

import ar.com.strellis.ampflower.data.model.ModelEntity;
import io.reactivex.rxjava3.core.Maybe;

public interface EntityDao<T>
{
    Maybe<T> listEntitiesObservable(String entityId);
}
