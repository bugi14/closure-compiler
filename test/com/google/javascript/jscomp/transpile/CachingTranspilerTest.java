/*
 * Copyright 2016 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp.transpile;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import junit.framework.TestCase;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests for {@link CachingTranspiler}. */
public final class CachingTranspilerTest extends TestCase {

  private Transpiler transpiler;
  @Mock(answer = RETURNS_SMART_NULLS) Transpiler delegate;

  private static final TranspileResult RESULT1 = new TranspileResult("foo.js", "bar", "baz", "");
  private static final TranspileResult RESULT2 = new TranspileResult("qux.js", "qux", "corge", "");
  private static final TranspileResult RESULT3 = new TranspileResult("bar.js", "baz", "xyzzy", "");

  @Override
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    transpiler = new CachingTranspiler(delegate);
  }

  public void testTranspileDelegates() {
    when(delegate.transpile("foo.js", "bar")).thenReturn(RESULT1);
    assertThat(transpiler.transpile("foo.js", "bar")).isSameAs(RESULT1);
  }

  public void testTranspileCaches() {
    when(delegate.transpile("foo.js", "bar")).thenReturn(RESULT1);
    assertThat(transpiler.transpile("foo.js", "bar")).isSameAs(RESULT1);
    assertThat(transpiler.transpile("foo.js", "bar")).isSameAs(RESULT1);
    verify(delegate, times(1)).transpile("foo.js", "bar");
  }

  public void testTranspileDependsOnBothPathAndCode() {
    when(delegate.transpile("foo.js", "bar")).thenReturn(RESULT1);
    when(delegate.transpile("food.js", "bar")).thenReturn(RESULT2);
    when(delegate.transpile("foo.js", "bard")).thenReturn(RESULT3);
    assertThat(transpiler.transpile("foo.js", "bar")).isSameAs(RESULT1);
    assertThat(transpiler.transpile("food.js", "bar")).isSameAs(RESULT2);
    assertThat(transpiler.transpile("foo.js", "bard")).isSameAs(RESULT3);
  }

  public void testRuntimeDelegates() {
    when(delegate.runtime()).thenReturn("xyzzy");
    assertThat(transpiler.runtime()).isSameAs("xyzzy");
  }

  public void testRuntimeCaches() {
    when(delegate.runtime()).thenReturn("xyzzy");
    assertThat(transpiler.runtime()).isSameAs("xyzzy");
    assertThat(transpiler.runtime()).isSameAs("xyzzy");
    verify(delegate, times(1)).runtime();
  }
}
