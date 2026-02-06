import { getReactBooks } from '@/api.ts'
import { useSuspenseQuery } from '@tanstack/react-query'
import { Book } from '@/components/book.tsx'
import { Grid } from '@/components/layout/grid.tsx'
import { BookPagination } from '@/components/book-pagination.tsx'
import { useState } from 'react'

export const BOOKS_PER_PAGE = 30

export const BookList = () => {
  const [page,setPage] = useState(1)
  const query = useSuspenseQuery({
    queryKey: ['books', page],
    queryFn: () => getReactBooks(BOOKS_PER_PAGE, page)
  })

  return (
    <>
      <Grid>
        {query.data.docs.map(({ key, ...rest }) => (
          <Book
            key={key}
            link={key}
            {...rest}
          />
        ))}
      </Grid>
      <BookPagination page={page} setPage={setPage} totalResults={query.data.numFound} />
    </>
  )
}