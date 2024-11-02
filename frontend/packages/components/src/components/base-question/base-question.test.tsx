import { type QuestionFragment } from '@tekmetric/graphql'
import { type RenderResult, render, screen } from '@testing-library/react'

import { formatCreatedDate } from '../../services/format-created-date/format-created-date'
import { BaseQuestion } from './base-question'
import { getDescriptions } from './services/get-description/get-description'

// Mock the formatCreatedDate and getDescriptions functions
jest.mock('../../services/format-created-date/format-created-date')
jest.mock('./services/get-description/get-description')
jest.mock('../question-actions/question-actions', () => ({
  QuestionActions: jest.fn(() => <div>Mocked QuestionActions</div>)
}))

const mockQuestion = {
  id: '1',
  title: 'Sample Question',
  status: 'open',
  createdAt: '2023-01-01T00:00:00Z',
  author: { firstName: 'John', lastName: 'Doe' },
  permissions: { canResolve: true }
} as unknown as QuestionFragment

const formatCreatedDateMock = formatCreatedDate as jest.Mock
const getDescriptionsMock = getDescriptions as jest.Mock

const renderComponent = (): RenderResult =>
  render(<BaseQuestion question={mockQuestion} hideViewButton={false} />)

describe('BaseQuestion', () => {
  beforeEach(() => {
    formatCreatedDateMock.mockClear()
    getDescriptionsMock.mockClear()
  })

  it('should render the question details and actions', () => {
    formatCreatedDateMock.mockReturnValue('January 1, 2023')
    getDescriptionsMock.mockReturnValue('Sample description')

    renderComponent()

    expect(screen.getByText('Sample Question')).toBeInTheDocument()
    expect(screen.getByText('John Doe (January 1, 2023)')).toBeInTheDocument()
    expect(screen.getByText('Sample description')).toBeInTheDocument()
    expect(screen.getByText('Mocked QuestionActions')).toBeInTheDocument()
  })
})
