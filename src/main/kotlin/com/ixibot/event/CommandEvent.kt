/*
 * Copyright (c) 2021, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.event

/**
 * Base class for bot command events.
 */
abstract class CommandEvent<out E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** Is the help flag present in the command? */
    val isHelp: Boolean,
    /** Is the command valid? */
    val isValid: Boolean
) {

    /**
     * Get a Builder pre-populated with the values in this event.
     *
     * @return a Builder pre-populated with the values in this event.
     */
    abstract fun toBuilder(): Builder<E, B>

    /**
     * Base class for CommandEvent builders.
     */
    abstract class Builder<out E : CommandEvent<E, B>, B : Builder<E, B>> {
        /** Mutable placeholder for CommandEvent#isHelp. */
        protected var isHelp: Boolean = false
            private set

        /** Mutable placeholder for CommandEvent#isValid. */
        protected var isValid: Boolean = true
            private set

        /**
         * Build event.
         *
         * @return immutable event object.
         */
        abstract fun build(): E

        abstract fun self(): B

        /**
         * Set isHelp.
         *
         * @param isHelp value.
         * @return this.
         */
        fun isHelp(isHelp: Boolean): B = this.apply { this.isHelp = isHelp }

        /**
         * Set isValid.
         *
         * @param isValid value.
         * @return this.
         */
        fun isValid(isValid: Boolean): B = this.apply { this.isValid = isValid }

        /**
         * Local override of apply which returns this#self instead of this.
         *
         * @return
         */
        private fun apply(function: () -> Unit): B {
            function()

            return this.self()
        }
    }
}
