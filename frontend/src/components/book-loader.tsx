import { Grid } from '@/components/layout/grid.tsx'
import { BOOKS_PER_PAGE } from '@/components/book-list.tsx'
import { Skeleton } from '@/components/ui/skeleton.tsx'


export const BookLoader =
  () => {
    return (
      <Grid>
        {Array.from({ length: BOOKS_PER_PAGE }).map((_, index) => (
          <Skeleton key={index} className="h-full w-full sm:h-[257px] md:h-[257px]"/>
        ))}
      </Grid>
    )
  }