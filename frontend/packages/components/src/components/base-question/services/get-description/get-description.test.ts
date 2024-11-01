import type {
  QuestionFragment,
  QuestionWithAnswersFragment
} from '@tekmetric/graphql'

import { getDescriptions } from './get-description'

describe('getDescription', () => {
  it('should return the description of the question', () => {
    expect(
      getDescriptions({
        description: 'description'
      } as QuestionWithAnswersFragment)
    ).toBe('description')
  })

  it('should return the short description of the question', () => {
    expect(
      getDescriptions({
        shortDescription: 'short description'
      } as QuestionFragment)
    ).toBe('short description...')
  })
})
