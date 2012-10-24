package org.sazabi.util

import com.twitter.concurrent.NamedPoolThreadFactory
import com.twitter.util.{ExecutorServiceFuturePool, Future, FuturePool, Try}

import java.util.concurrent.Executors

trait AsyncPool {
  def apply[A](f: => A): Future[A] = pool(f)

  val pool: FuturePool
}

trait ImmediatePool extends AsyncPool {
  override val pool: FuturePool = FuturePool.immediatePool
}

trait CachedAsyncPool extends AsyncPool {
  def threadFactoryName: String

  override lazy val pool: FuturePool = new ExecutorServiceFuturePool(
    Executors.newCachedThreadPool(new NamedPoolThreadFactory(threadFactoryName)))
}

trait FixedAsyncPool extends AsyncPool {
  def threadFactoryName: String
  def poolSize: Int

  override lazy val pool: FuturePool = new ExecutorServiceFuturePool(
    Executors.newFixedThreadPool(poolSize,
      new NamedPoolThreadFactory(threadFactoryName)))
}
