export function copyValue(from, to) {
  for (let key of Object.keys(to)) {
    if (from[key] !== undefined) {
      to[key] = from[key]
    }
  }
}