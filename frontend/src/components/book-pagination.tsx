import { type Dispatch, type SetStateAction } from 'react'
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import { BOOKS_PER_PAGE } from '@/components/book-list'

interface BookPaginationProps {
  page: number
  setPage: Dispatch<SetStateAction<number>>
  totalResults: number
}

export const BookPagination = ({ page, setPage, totalResults }: BookPaginationProps) => {
  const totalPages = Math.ceil(totalResults / BOOKS_PER_PAGE)

  // Calculate which pages to display (3 pages total)
  const getPageNumbers = () => {
    if (page === 1) {
      // First page: show pages 1, 2, 3
      return [1, 2, 3].filter(p => p <= totalPages)
    } else if (page === totalPages) {
      // Last page: show pages totalPages-2, totalPages-1, totalPages
      const start = Math.max(1, totalPages - 2)
      return Array.from({ length: 3 }, (_, i) => start + i).filter(p => p <= totalPages)
    } else {
      // Middle pages: active page in the middle with one on each side
      return [page - 1, page, page + 1].filter(p => p >= 1 && p <= totalPages)
    }
  }

  const pageNumbers = getPageNumbers()
  const showEllipsisEnd = pageNumbers[pageNumbers.length - 1] < totalPages

  const handlePageClick = (newPage: number) => {
    setPage(newPage)
  }

  const handlePrevious = () => {
    if (page > 1) {
      setPage(page - 1)
    }
  }

  const handleNext = () => {
    if (page < totalPages) {
      setPage(page + 1)
    }
  }

  return (
    <Pagination className='sticky bottom-4 z-30'>
      <PaginationContent className="rounded-2xl bg-white p-2 shadow-pagination w-full justify-center md:w-auto">
        <PaginationItem>
          <PaginationPrevious
            href="#"
            onClick={(e) => {
              e.preventDefault()
              handlePrevious()
            }}
            className={page === 1 ? 'pointer-events-none opacity-50' : ''}
          />
        </PaginationItem>
        {pageNumbers.map((pageNum) => (
          <PaginationItem key={pageNum}>
            <PaginationLink
              href="#"
              onClick={(e) => {
                e.preventDefault()
                handlePageClick(pageNum)
              }}
              isActive={pageNum === page}
            >
              {pageNum}
            </PaginationLink>
          </PaginationItem>
        ))}
        {showEllipsisEnd && (
          <PaginationItem>
            <PaginationEllipsis />
          </PaginationItem>
        )}
        <PaginationItem>
          <PaginationNext
            href="#"
            onClick={(e) => {
              e.preventDefault()
              handleNext()
            }}
            className={page === totalPages ? 'pointer-events-none opacity-50' : ''}
          />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  )
}
