import { createContext, useContext } from 'react'
import { cn } from '@/lib/classnames/cn'

export type TableColumn = {
  key: string
  label: string
  align?: 'left' | 'right'
  className?: string
}

type TableSectionProps = {
  children: React.ReactNode
  className?: string
}

type TableHeaderProps = React.ComponentPropsWithoutRef<'thead'>
type TableBodyProps = React.ComponentPropsWithoutRef<'tbody'>
type TableRowProps = React.ComponentPropsWithoutRef<'tr'>
type TableHeadElementProps = React.ComponentPropsWithoutRef<'th'>
type TableCellElementProps = React.ComponentPropsWithoutRef<'td'>
type TableCaptionProps = React.ComponentPropsWithoutRef<'caption'>

type TableCardProps = {
  children: React.ReactNode
  scrollX?: boolean
}

type TableRootProps = {
  children: React.ReactNode
  columns: TableColumn[]
  className?: string
}

type TableHeadProps = {
  children: React.ReactNode
  align?: 'left' | 'right'
  className?: string
} & Omit<TableHeadElementProps, 'children' | 'className'>

type TableCellProps = {
  children: React.ReactNode
  align?: 'left' | 'right'
  className?: string
} & Omit<TableCellElementProps, 'children' | 'className'>

type TableErrorRowProps = {
  message: string
}

type TableSkeletonRowsProps = {
  count?: number
}

const TableColumnsContext = createContext<TableColumn[] | null>(null)

function useTableColumns() {
  const columns = useContext(TableColumnsContext)
  if (!columns) {
    throw new Error('Table components must be used within Table.Root')
  }
  return columns
}

function TableCard({ children, scrollX = false }: TableCardProps) {
  return (
    <div className="tm-card overflow-hidden">
      {scrollX ? <div className="overflow-x-auto">{children}</div> : children}
    </div>
  )
}

function TableRoot({ children, columns, className = 'w-full text-sm' }: TableRootProps) {
  return (
    <TableColumnsContext.Provider value={columns}>
      <table className={className}>{children}</table>
    </TableColumnsContext.Provider>
  )
}

function TableHeader({ children, className, ...props }: TableHeaderProps) {
  return (
    <thead className={className} {...props}>
      {children}
    </thead>
  )
}

function TableBody({ children, className = 'divide-y divide-border', ...props }: TableBodyProps) {
  return (
    <tbody className={className} {...props}>
      {children}
    </tbody>
  )
}

function TableRow({ children, className, ...props }: TableRowProps) {
  return (
    <tr className={className} {...props}>
      {children}
    </tr>
  )
}

function TableHead({ children, align = 'left', className, ...props }: TableHeadProps) {
  return (
    <th
      className={cn('px-6 py-4', align === 'right' ? 'text-right' : 'text-left', className)}
      {...props}
    >
      {children}
    </th>
  )
}

function TableCell({ children, align = 'left', className, ...props }: TableCellProps) {
  return (
    <td className={cn('px-6 py-4', align === 'right' && 'text-right', className)} {...props}>
      {children}
    </td>
  )
}

function TableCaption({ children, className, ...props }: TableCaptionProps) {
  return (
    <caption className={className} {...props}>
      {children}
    </caption>
  )
}

function TableHeaderRow() {
  const columns = useTableColumns()
  return (
    <TableRow className="border-b border-border bg-slate-50/70 text-xs font-semibold uppercase tracking-wider text-slate-500">
      {columns.map((column) => (
        <TableHead key={column.key} align={column.align} className={column.className}>
          {column.label}
        </TableHead>
      ))}
    </TableRow>
  )
}

function TableErrorRow({ message }: TableErrorRowProps) {
  const columns = useTableColumns()
  return (
    <TableRow>
      <TableCell colSpan={columns.length} className="py-8 text-center text-sm text-red-600">
        {message}
      </TableCell>
    </TableRow>
  )
}

function TableSkeletonRows({ count = 3 }: TableSkeletonRowsProps) {
  const columns = useTableColumns()
  const widths = ['w-36', 'w-28', 'w-24', 'w-20', 'w-16', 'w-12']

  return (
    <>
      {Array.from({ length: count }).map((_, rowIndex) => (
        <TableRow key={rowIndex} className="animate-pulse">
          {columns.map((column, colIndex) => {
            const barClass = column.key.toLowerCase().includes('status')
              ? 'h-5 w-24 rounded-full bg-slate-200'
              : `h-4 ${widths[colIndex % widths.length]} rounded bg-slate-200`

            return (
              <TableCell key={column.key} align={column.align}>
                <div className={cn(column.align === 'right' && 'ml-auto', barClass)} />
              </TableCell>
            )
          })}
        </TableRow>
      ))}
    </>
  )
}

export const Table = Object.assign(TableRoot, {
  Card: TableCard,
  Root: TableRoot,
  Header: TableHeader,
  Body: TableBody,
  Row: TableRow,
  Head: TableHead,
  Cell: TableCell,
  Caption: TableCaption,
  HeaderRow: TableHeaderRow,
  ErrorRow: TableErrorRow,
  SkeletonRows: TableSkeletonRows,
})
