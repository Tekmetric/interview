import { render, screen } from '@testing-library/react';
import Chapter from './chapter';

describe('Chapter component', () => {
  const mockChapter = {
    chapter_number: 1,
    name_meaning: 'Name meaning 1',
    chapter_summary: 'Summary of Chapter 1',
  };

  beforeEach(() => {
    render(
      <Chapter
        chapter_number={mockChapter.chapter_number}
        name_meaning={mockChapter.name_meaning}
        chapter_summary={mockChapter.chapter_summary}
      />,
    );
  });

  it('should render chapter number correctly', () => {
    const chapterNumberElement = screen.getByText('Chapter 1');
    expect(chapterNumberElement).toBeInTheDocument();
  });

  it('should render name meaning correctly', () => {
    const nameMeaningElement = screen.getByText('Name meaning 1');
    expect(nameMeaningElement).toBeInTheDocument();
  });

  it('should render chapter summary correctly', () => {
    const chapterSummaryElement = screen.getByText('Summary of Chapter 1');
    expect(chapterSummaryElement).toBeInTheDocument();
  });

  it('should have a divider with class "w-1/3"', () => {
    const dividerElement = screen.getByTestId('divider');
    expect(dividerElement).toHaveClass('w-1/3');
  });
});
