import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import eslintPluginPrettier from 'eslint-plugin-prettier';
import { fixupPluginRules } from '@eslint/compat';

export default tseslint.config(
    { ignores: ['node_modules/', 'build/', 'dist'] },
    {
        extends: [js.configs.recommended, ...tseslint.configs.recommended],
        files: ['**/*.{ts,tsx}'],
        languageOptions: {
            ecmaVersion: 2020,
            globals: globals.browser,
        },
        plugins: {
            'react-hooks': fixupPluginRules(reactHooks),
            'react-refresh': reactRefresh,
            prettier: eslintPluginPrettier
        },
        rules: {
            ...reactHooks.configs.recommended.rules,
            'react-refresh/only-export-components': [
                'warn',
                { allowConstantExport: true },
            ],
            'react-hooks/exhaustive-deps': 'warn'
        },
    }
);