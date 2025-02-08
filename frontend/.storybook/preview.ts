// .storybook/preview.ts
import '../src/app/globals.css'
import { mswDecorator } from 'msw-storybook-addon'

export const decorators = [mswDecorator]

export const parameters = {
  actions: {
    handles: ['click', 'mouseover'],
  },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
}
