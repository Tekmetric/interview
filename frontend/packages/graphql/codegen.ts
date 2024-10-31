import type { CodegenConfig } from '@graphql-codegen/cli'

const config: CodegenConfig = {
  schema: 'http://localhost:5089/graphql',
  documents: ['src/**/*.ts'],
  generates: {
    './src/__generated__/': {
      preset: 'client',
      presetConfig: {
        gqlTagName: 'gql'
      }
    },
    './src/__generated__/types.ts': {
      plugins: ['typescript', 'typescript-operations']
    }
  },
  ignoreNoDocuments: true
}

export default config
