import { Test, TestingModule } from '@nestjs/testing';
import { UserContext } from '@tekmetric/core';
import { CreateAnswerDto } from '../objects/dto/create-answer.dto';
import { Answer } from '../objects/models/answer.model';
import { Author } from '../objects/models/author.model';
import { AnswerServices } from '../services/answer.service';
import { AnswerResolver } from './answer.resolver';

describe('AnswerResolver', () => {
  let answerResolver: AnswerResolver;
  let answerService: AnswerServices;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AnswerResolver,
        {
          provide: AnswerServices,
          useValue: {
            getAuthorById: jest.fn(),
            createAnswer: jest.fn(),
          },
        },
      ],
    }).compile();

    answerResolver = module.get<AnswerResolver>(AnswerResolver);
    answerService = module.get<AnswerServices>(AnswerServices);
  });

  describe('author', () => {
    it('should return the author of the answer', async () => {
      const answer: Answer = {
        id: '1',
        authorId: '1',
        description: 'Answer content',
        createdAt: new Date(),
      };
      const author: Author = {
        id: '1',
        firstName: 'John',
        lastName: 'Doe',
      };

      jest.spyOn(answerService, 'getAuthorById').mockResolvedValue(author);

      const result = await answerResolver.author(answer);

      expect(answerService.getAuthorById).toHaveBeenCalledWith(answer.authorId);
      expect(result).toEqual(author);
    });
  });

  describe('createAnswer', () => {
    it('should create and return an answer', async () => {
      const user: UserContext = {
        userId: '1',
        role: 'USER',
      };
      const createAnswerDto: CreateAnswerDto = {
        questionId: '1',
        description: 'Answer content',
      };
      const answer: Answer = {
        id: '1',
        authorId: user.userId,
        description: createAnswerDto.description,
        createdAt: new Date(),
      };

      jest.spyOn(answerService, 'createAnswer').mockResolvedValue(answer);

      const result = await answerResolver.createAnswer(user, createAnswerDto);

      expect(answerService.createAnswer).toHaveBeenCalledWith(
        user.userId,
        createAnswerDto,
      );
      expect(result).toEqual(answer);
    });
  });
});
