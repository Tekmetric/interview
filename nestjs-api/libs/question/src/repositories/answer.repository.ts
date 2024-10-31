import { Injectable } from '@nestjs/common';
import { PrismaService } from '@tekmetric/database';
import { Author } from '../objects/models/author.model';
import { CreateAnswerDto } from '../objects/dto/create-answer.dto';

@Injectable()
export class AnswerRepository {
  constructor(private readonly prisma: PrismaService) {}

  public async getAuthorById(id: string): Promise<Author> {
    const result = await this.prisma.user.findUnique({
      where: { id },
    });

    return {
      id: result.id,
      firstName: result.firstName,
      lastName: result.lastName,
    };
  }

  public async createAnswer(authorId: string, data: CreateAnswerDto) {
    return await this.prisma.answer.create({
      data: {
        ...data,
        authorId,
      },
    });
  }
}
