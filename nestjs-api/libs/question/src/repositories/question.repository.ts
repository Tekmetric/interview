import { Injectable } from '@nestjs/common';
import { PrismaService } from '@tekmetric/database';
import { Author } from '../objects/models/author.model';
import { CreateQuestionDto } from '../objects/dto/create-question.dto';

@Injectable()
export class QuestionRepository {
  constructor(private readonly prisma: PrismaService) {}

  public async getQuestions() {
    return await this.prisma.question.findMany();
  }

  public async getQuestionById(id: string) {
    return await this.prisma.question.findUnique({
      where: { id },
    });
  }

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

  public async getAnswersByQuestionId(questionId: string) {
    return await this.prisma.answer.findMany({
      where: {
        questionId,
      },
    });
  }

  public async createQuestion(authorId: string, data: CreateQuestionDto) {
    return await this.prisma.question.create({
      data: {
        ...data,
        authorId,
      },
    });
  }
}
