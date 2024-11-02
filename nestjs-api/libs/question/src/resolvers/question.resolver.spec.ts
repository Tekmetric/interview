import { Test, TestingModule } from '@nestjs/testing';
import { QuestionResolver } from './question.resolver';
import { QuestionService } from '../services/question.service';
import { QuestionStatus } from '@tekmetric/database';
import { CreateQuestionDto } from '../objects/dto/create-question.dto';
import { Answer } from '../objects/models/answer.model';
import { Author } from '../objects/models/author.model';
import { Question } from '../objects/models/question.model';
import { QuestionPermissions } from '../objects/models/question-permissions.model';
import { UserContext } from '@tekmetric/core';

describe('QuestionResolver', () => {
  let questionResolver: QuestionResolver;
  let questionService: QuestionService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        QuestionResolver,
        {
          provide: QuestionService,
          useValue: {
            getQuestionsByStatus: jest.fn(),
            getQuestionById: jest.fn(),
            getShortDescription: jest.fn(),
            getAuthorById: jest.fn(),
            getAnswersByQuestionId: jest.fn(),
            createQuestion: jest.fn(),
            resolveQuestion: jest.fn(),
          },
        },
      ],
    }).compile();

    questionResolver = module.get<QuestionResolver>(QuestionResolver);
    questionService = module.get<QuestionService>(QuestionService);
  });

  describe('questions', () => {
    it('should return an array of questions', async () => {
      const status = QuestionStatus.PENDING;
      const questions: Question[] = [
        {
          id: '1',
          authorId: '1',
          title: 'Question 1',
          description: 'Description 1',
          createdAt: new Date(),
          status,
        },
      ];

      jest
        .spyOn(questionService, 'getQuestionsByStatus')
        .mockResolvedValue(questions);

      const result = await questionResolver.questions(status);

      expect(questionService.getQuestionsByStatus).toHaveBeenCalledWith(status);
      expect(result).toEqual(questions);
    });
  });

  describe('question', () => {
    it('should return a question', async () => {
      const id = '1';
      const question: Question = {
        id,
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };

      jest
        .spyOn(questionService, 'getQuestionById')
        .mockResolvedValue(question);

      const result = await questionResolver.question(id);

      expect(questionService.getQuestionById).toHaveBeenCalledWith(id);
      expect(result).toEqual(question);
    });
  });

  describe('shortDescription', () => {
    it('should return a short description', async () => {
      const question: Question = {
        id: '1',
        authorId: '1',
        title: 'Question 1',
        description:
          'This is a long description that should be shortened to fifty words or less.',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };

      const shortDescription =
        'This is a long description that should be shortened to fifty words or';

      jest
        .spyOn(questionService, 'getShortDescription')
        .mockResolvedValue(shortDescription);

      const result = await questionResolver.shortDescription(question);

      expect(questionService.getShortDescription).toHaveBeenCalledWith(
        question,
      );
      expect(result).toEqual(shortDescription);
    });
  });

  describe('author', () => {
    it('should return the author of the question', async () => {
      const question: Question = {
        id: '1',
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };
      const author: Author = {
        id: '1',
        firstName: 'John',
        lastName: 'Doe',
      };

      jest.spyOn(questionService, 'getAuthorById').mockResolvedValue(author);

      const result = await questionResolver.author(question);

      expect(questionService.getAuthorById).toHaveBeenCalledWith(
        question.authorId,
      );
      expect(result).toEqual(author);
    });
  });

  describe('answers', () => {
    it('should return an array of answers', async () => {
      const question: Question = {
        id: '1',
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };
      const answers: Answer[] = [
        {
          id: '1',
          authorId: '1',
          createdAt: new Date(),
          description: 'Answer 1',
        },
      ];

      jest
        .spyOn(questionService, 'getAnswersByQuestionId')
        .mockResolvedValue(answers);

      const result = await questionResolver.answers(question);

      expect(questionService.getAnswersByQuestionId).toHaveBeenCalledWith(
        question.id,
      );
      expect(result).toEqual(answers);
    });
  });

  describe('permissions', () => {
    it('should return the permissions for the question', async () => {
      const question: Question = {
        id: '1',
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };
      const user: UserContext = {
        userId: '1',
        role: 'ADMIN',
      };
      const permissions: QuestionPermissions = {
        id: question.id,
        canResolve: true,
      };

      const result = await questionResolver.permissions(question, user);

      expect(result).toEqual(permissions);
    });
  });

  describe('createQuestion', () => {
    it('should create and return a question', async () => {
      const user: UserContext = {
        userId: '1',
        role: 'USER',
      };
      const createQuestionDto: CreateQuestionDto = {
        title: 'Question 1',
        description: 'Description 1',
      };
      const question: Question = {
        id: '1',
        authorId: user.userId,
        title: createQuestionDto.title,
        description: createQuestionDto.description,
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };

      jest.spyOn(questionService, 'createQuestion').mockResolvedValue(question);

      const result = await questionResolver.createQuestion(
        user,
        createQuestionDto,
      );

      expect(questionService.createQuestion).toHaveBeenCalledWith(
        user.userId,
        createQuestionDto,
      );
      expect(result).toEqual(question);
    });
  });

  describe('resolveQuestion', () => {
    it('should resolve and return a question', async () => {
      const id = '1';
      const question: Question = {
        id,
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.COMPLETED,
      };

      jest
        .spyOn(questionService, 'resolveQuestion')
        .mockResolvedValue(question);

      const result = await questionResolver.resolveQuestion(id);

      expect(questionService.resolveQuestion).toHaveBeenCalledWith(id);
      expect(result).toEqual(question);
    });
  });
});
