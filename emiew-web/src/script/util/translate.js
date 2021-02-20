import db from '@/data/tagTranslation.json'

export function translateTagGroup(group) {
  let d = db.data.filter((d) => d.namespace === 'rows')[0].data[group]
  return d ? d.name : group
}

export function translateLanguage(language) {
  let d = db.data.filter((d) => d.namespace === 'language')[0].data[
    language.toLowerCase()
  ]
  return d ? d.name : language
}

export function translateTag(group, tag) {
  let g = db.data.filter((d) => d.namespace === group)[0]
  if (!g) {
    return tag
  }

  let d = g.data[tag]
  if (!d) {
    return tag
  }

  return d.name
}

export function translateCategory(category) {
  category = category.toLowerCase().replace(/\s+/g, '')
  let d = db.data.filter((d) => d.namespace === 'reclass')[0].data[category]
  return d ? d.name : category
}
