package ir.garm.cache.service;

import ir.garm.cache.domain.dto.ResponseData;

public interface BaseUpstreamService<I, O> extends AuthBaseService {
    ResponseData<O> call(I body);
}
