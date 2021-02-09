<template>
  <div class="em-input" :class="{ 'text-only': !icon }">
    <fa-icon class="em-input-prefix-icon" v-if="icon" :name="icon" />
    <fa-icon
      class="em-input-clear-icon"
      v-show="!isValueEmpty"
      name="times-circle"
      @click="$emit('input', '')"
    />
    <input
      :type="type"
      :value="value"
      :placeholder="placeholder"
      @input="$emit('input', $event.target.value)"
    />
  </div>
</template>

<script>
export default {
  name: 'EmInput',
  props: {
    type: {
      type: String,
      default: 'text',
    },
    icon: {
      type: String,
    },
    placeholder: {
      default: '',
    },
    value: {},
  },
  computed: {
    isValueEmpty() {
      return (
        this.value === undefined || this.value === null || this.value === ''
      )
    },
  },
}
</script>

<style>
.em-input {
  display: inline-block;
  position: relative;
  width: 100%;
  height: 40px;
  vertical-align: top;
}

.em-input-prefix-icon {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 10px;
  margin: auto;
}

.em-input-clear-icon {
  position: absolute;
  top: 0;
  bottom: 0;
  right: 10px;
  margin: auto;
}

.em-input input {
  width: 100%;
  height: 100%;
  border: 1px solid #ddd;
  background-color: #eee;
  padding: 0 36px;
  border-radius: 5px;
  transition: border 0.1s ease;
}

.em-input input:focus {
  border: 1px solid #0af;
}

.em-input.text-only input {
  padding: 0 36px 0 10px;
}
</style>
