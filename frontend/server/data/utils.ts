type UpdateBuilder = {
  updates: string[]
  values: any[]
}

type FieldMapping<T> = {
  field: keyof T
  column: string
  transform?: (value: any) => any
}

export function buildUpdateFields<T>(
  data: Partial<T>,
  mappings: FieldMapping<T>[],
): UpdateBuilder {
  const updates: string[] = []
  const values: any[] = []

  for (const { field, column, transform } of mappings) {
    if (data[field] !== undefined) {
      updates.push(`${column} = ?`)
      const value = transform ? transform(data[field]) : data[field]
      values.push(value)
    }
  }

  return { updates, values }
}
