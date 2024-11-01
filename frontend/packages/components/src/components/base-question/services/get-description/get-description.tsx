import type {
  QuestionFragment,
  QuestionWithAnswersFragment
} from '@tekmetric/graphql'

export const getDescriptions = (
  question: QuestionFragment | QuestionWithAnswersFragment
): string => {
  const description = 'description' in question ? question.description : null

  if (description) {
    return description
  }

  return `${question.shortDescription}...`
}
