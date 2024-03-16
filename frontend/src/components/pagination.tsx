import { Icon } from '@iconify/react'
import tw, { styled } from 'twin.macro'

const Button = styled.button(({ variant }: Pick<PaginationProps, 'variant'>) => [
  tw`text-white`,
  tw`bg-slate-500 hover:bg-slate-700 disabled:bg-slate-800`,
  variant === 'small' && tw`p-[4px]`,
  variant === 'normal' && tw`p-[16px]`,
])

type PaginationProps = {
  page?: number
  totalPages?: number
  variant: 'small' | 'normal'

  onPageChange: (page: number) => void
}

const Pagination = ({ variant, page, totalPages, onPageChange }: PaginationProps): JSX.Element => {
  return (
    <div tw="w-fit flex flex-row items-center justify-center rounded-[8px] overflow-hidden">
      <Button variant={variant} disabled={page == null || page === 0} onClick={() => onPageChange(page! - 1)}>
        <Icon icon={'tdesign:chevron-left-double'} width={24} />
      </Button>
      <Button variant={variant}>Page {(page ?? 0) + 1}</Button>
      <Button
        variant={variant}
        disabled={page == null || totalPages == null || page + 1 >= totalPages}
        onClick={() => onPageChange(page! + 1)}
      >
        <Icon icon={'tdesign:chevron-right-double'} width={24} />
      </Button>
    </div>
  )
}

export default Pagination
