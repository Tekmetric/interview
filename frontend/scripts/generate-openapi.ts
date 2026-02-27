import { writeFileSync } from 'node:fs'
import { resolve } from 'node:path'
import { openApiSpec } from '../lib/api/contracts/openapi'

const outputFile = resolve(process.cwd(), 'openapi.json')
writeFileSync(outputFile, `${JSON.stringify(openApiSpec, null, 2)}\n`, 'utf-8')
