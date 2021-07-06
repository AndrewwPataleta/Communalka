package com.patstudio.communalka.domain.base

import com.patstudio.communalka.domain.Result

interface BaseUseCase<T : Any, R: Any> {
  suspend operator fun invoke(param: T): Result<R>
}