'use strict';
	
	var m = angular.module('MyMod', ['ngResource']);
	m.factory('Server', function($resource){
			return $resource('/:oper.json');
	});
	m.controller('MyContr', function MyContr($scope, Server) {
		var questions = null;
		$scope.data = {
			mode: 'wellcome', // 'results' 'questions'
			curI: 0,
			total: 0,
			curQ: null,
			res: null,
			title: null
		};
		function init() {
			/*questions = [
			  {
				question: 'Создать папку на Рабочем столе с именем Удача',
				answers: [
				  {
					text: 'Щелкнуть правой кнопкой мыши на свободном месте Рабочего стола  Выбрать команду  <Создать> - <Папка>-<ОК>. Ввести имя папки "Удача"'
				  },
				  {
					text: 'Щелкнуть левой кнопкой мыши на свободном месте Рабочего стола Выбрать команду  <Создать> - <Папка>-<ОК>. Ввести имя папки "Удача"'
				  },
				  {
					text: 'Щелкнуть правой кнопкой мыши на свободном месте Рабочего стола Выбрать команду <Создать>-<Ярлык>-<ОК>. Ввести имя папки "Удача"'
				  }
				]
			  }
			];
			'Название теста'
			*/
			Server.get({oper:'init'}, function(data) {
				setData(data);
			});
			function setData(data) {
				questions = data.questions;
				$scope.data.total = data.questions.length;
				$scope.data.title = data.title;
				angular.forEach(questions, function(v,i) { v.id = i; });
			}
		}
		$scope.start = function() {
			$scope.data.mode = 'questions';
			selectQ(questions, 0);
		}
		function selectQ(questions, i) {
			$scope.data.curI = i;
			$scope.data.curQ = questions[i]
			angular.forEach($scope.data.curQ.answers, function(v,i) { v.id = i; });
		}
		$scope.nextQ = function() {
			var nextI = $scope.data.curI+1;
			if (nextI < $scope.data.total) {
				selectQ(questions, nextI);
			} else {
				submitAnswers(questions);
			}
		};
		function submitAnswers(questions) {
			var answers = [];
			angular.forEach(questions, function(v,i) {
				v.id = i;
				answers.push(v);
				//answers.push({id: i, a: v.a, question: v.question});
			});
			console.log('Check', questions, answers)
			checkResults(answers, showResults);
		}
		function checkResults(answers, showResults) {
			Server.save({oper:'checkResults'}, {questions: answers}, function(data) {
				/*{
					right: 7,
					total: 10,
					mark: 4
				}*/
				data.questions = answers;
				showResults(data);
			});
			
		}
		function showResults(res) {
			$scope.data.mode = 'results';
			$scope.data.res = res;
		}
		init();
	});