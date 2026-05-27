package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Expens;
import com.example.demo.entity.User;
import com.example.demo.model.Account;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ExpensRepository;
import com.example.demo.repository.UserRepository;

@Controller

public class HomeController {

	private final ExpensRepository expensRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final Account account;
	private final HttpSession session;

	public HomeController(ExpensRepository expensRepository,
			CategoryRepository categoryRepository,
			UserRepository userRepository,
			Account account,
			HttpSession session) {
		this.expensRepository = expensRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.account = account;
		this.session = session;
	}

	@GetMapping("/home")
	public String index(Model model) {

		List<Expens> expensList = expensRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();

		model.addAttribute("expens", expensList);
		model.addAttribute("category", categoryList);

		List<Object[]> categorySum = expensRepository.sumPriceByCategory();
		Integer totalSum = expensRepository.findAll()
				.stream()
				.mapToInt(Expens::getPrice)
				.sum();
		model.addAttribute("categorySum", categorySum);
		model.addAttribute("totalSum", totalSum);

		List<Integer> sumprice = expensRepository.sumExpens();
		List<String> categoryname = expensRepository.categoryname();

		List<String> label = new ArrayList<>();
		List<Integer> data = new ArrayList<Integer>();

		label.addAll(categoryname);
		data.addAll(sumprice);

		model.addAttribute("label", label);
		model.addAttribute("data", data);

		Integer keisan = account.getIncome() - totalSum;
		model.addAttribute("keisan", keisan);

		if (keisan < 0) {
			model.addAttribute("yabai");
		}

		return "home";
	}

	@GetMapping("/expens/add")
	public String addExpens(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		return "addExpens";
	}

	@PostMapping("/expens/add")
	public String addedExpens(
			@RequestParam(defaultValue = "") Category category,
			@RequestParam(defaultValue = "") LocalDate date,
			@RequestParam(defaultValue = "") Integer price1,
			@RequestParam(defaultValue = "") String name,
			Model model) {

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		List<String> errorList = new ArrayList<>();

		if (category == null) {
			errorList.add("カテゴリが選択されていません");
		}

		if (date == null) {
			errorList.add("日付が選択されていません");
		}

		if (price1 == null) {
			errorList.add("金額が入力されていません");
		}

		if (name == null || name.length() == 0) {
			errorList.add("項目名が入力されていません");
		}

		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			return "addExpens";
		}

		Integer price = price1 * 100;

		Expens expens = new Expens(category, date, price, name);

		expensRepository.save(expens);

		return "redirect:/home";
	}

	@PostMapping("/expens/{id}/delete")
	public String deleteExpens(@PathVariable Integer id) {

		expensRepository.deleteById(id);

		return "redirect:/home";
	}

	@GetMapping("/expens/{id}/edit")
	public String edit(@PathVariable Integer id, Model model) {

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		Expens expens = expensRepository.findById(id).get();
		model.addAttribute("expens", expens);

		return "editExpens";
	}

	@PostMapping("/expens/{id}/edit")
	public String update(
			@PathVariable Integer id,
			@RequestParam(defaultValue = "") Category category,
			@RequestParam(defaultValue = "") LocalDate date,
			@RequestParam(defaultValue = "") Integer price1,
			@RequestParam(defaultValue = "") String name,
			Model model) {

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		List<String> errorList = new ArrayList<>();

		if (category == null) {
			errorList.add("カテゴリが選択されていません");
		}

		if (date == null) {
			errorList.add("日付が選択されていません");
		}

		if (price1 == null) {
			errorList.add("金額が入力されていません");
		}

		if (name == null || name.length() == 0) {
			errorList.add("項目名が入力されていません");
		}

		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			return "addExpens";
		}

		Integer price = price1 * 100;

		Expens expens = expensRepository.findById(id).get();
		expens.setCategory(category);
		expens.setDate(date);
		expens.setPrice(price);
		expens.setName(name);

		expensRepository.save(expens);

		return "redirect:/home";
	}

	@GetMapping("/category/add")
	public String addCategory(Model model) {

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		return "addCategory";
	}

	@PostMapping("/category/add")
	public String addedExpens(
			@RequestParam(defaultValue = "") String name,
			Model model) {

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categories", categoryList);

		if (name == null || name.length() == 0) {
			model.addAttribute("message", "入力してください");
			return "addCategory";
		}

		Category category = new Category(name);

		categoryRepository.save(category);

		return "redirect:/category/add";
	}

	@PostMapping("/category/{id}/delete")
	public String deleteCategory(@PathVariable Integer id) {

		categoryRepository.deleteById(id);

		return "redirect:/category/add";
	}

	@GetMapping({ "/", "/logout" })
	public String login() {
		session.invalidate();
		return "login";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam(defaultValue = "") String name,
			Model model) {

		List<User> username = userRepository.findByName(name);

		if (name == null || name.length() == 0) {
			model.addAttribute("message", "名前を入力してください");
			return "login";
		}

		if (username == null || username.size() == 0) {
			model.addAttribute("message", "名前が存在しません");
			return "login";
		}

		List<Expens> expensList = expensRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();

		model.addAttribute("expens", expensList);
		model.addAttribute("category", categoryList);

		List<Object[]> categorySum = expensRepository.sumPriceByCategory();
		Integer totalSum = expensRepository.findAll()
				.stream()
				.mapToInt(Expens::getPrice)
				.sum();
		model.addAttribute("categorySum", categorySum);
		model.addAttribute("totalSum", totalSum);

		List<Integer> sumprice = expensRepository.sumExpens();
		List<String> categoryname = expensRepository.categoryname();

		List<String> label = new ArrayList<>();
		List<Integer> data = new ArrayList<Integer>();

		label.addAll(categoryname);
		data.addAll(sumprice);

		model.addAttribute("label", label);
		model.addAttribute("data", data);

		Integer income = username.get(0).getIncome();

		account.setName(name);
		account.setIncome(income);

		Integer keisan = account.getIncome() - totalSum;
		model.addAttribute("keisan", keisan);

		return "home";
	}

	@GetMapping("/user")
	public String addUser() {
		return "user";
	}

	@PostMapping("/user")
	public String logined(
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") Integer income) {
		
		User user = userRepository.findByName(name).get(0);
		
		user.setIncome(income);
		
		userRepository.save(user);

		return "login";
	}
	
	@GetMapping("/adduser")
	public String adduser() {
		return "addUser";
	}
	
	@PostMapping("/adduser")
	public String addusers(
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") Integer income) {
		
		User user = new User(name, income);
		
		userRepository.save(user);
		
		return "login";
	}

}
