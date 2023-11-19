package com.ixibot.event

/**
 * Base class for bot command events.
 */
abstract class CommandEvent<out E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** Is the help flag present in the command? */
    val isHelp: Boolean,
    /** Is the command valid? */
    val isValid: Boolean,
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
