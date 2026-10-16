#include "runtime.h"
#include <iostream>
#include <stdexcept>

static void check(bool condition, const char * message) { if (!condition) throw std::runtime_error(message); }
template<class F> static void rejects(F function, const char * message) {
    bool rejected = false;
    try { function(); } catch (const std::exception &) { rejected = true; }
    check(rejected, message);
}
int main(int argc, char ** argv) {
    if (argc != 2) return 2;
    try {
        meshmind::Runtime engine;
        rejects([&] { engine.load(argv[1], 262144, 2, true); }, "Unsafe context accepted");
        rejects([&] { engine.load(argv[1], 2048, 2, true, "INVALID"); }, "Manifest format mismatch accepted");
        engine.load(argv[1], 2048, 2, true, "Q2_0", {"<|im_start|>", "<|im_end|>", "<think>", "</think>"});
        meshmind::Settings s;
        s.temperature = 0;
        s.max_tokens = 16;
        common_chat_msg message; message.role = "user";
        message.content = "What is 2 + 2? Answer with just the number.";
        auto run = [&] {
            engine.prepare();
            std::string output;
            auto stats = engine.generate({message}, s, [&](const std::string & part) { output += part; return true; });
            check(stats.generated > 0 && !stats.cancelled, "No actual generation");
            return output;
        };
        const auto first = run();
        const auto second = run();
        check(first == "4", "Arithmetic output differs from known reference");
        check(first == second, "Context reset leaked recurrent state");
        std::cout << "Deterministic output: " << first << '\n';
        engine.prepare();
        auto cancelled = engine.generate({message}, s, [](const std::string &) { return false; });
        check(cancelled.cancelled && cancelled.generated == 1, "Callback cancellation failed");
        engine.prepare();
        engine.cancel();
        auto before_decode = engine.generate({message}, s, [](const std::string &) { return true; });
        check(before_decode.cancelled && before_decode.generated == 0, "Pre-start cancellation was lost");
        engine.prepare();
        auto invalid = s; invalid.top_p = 0;
        rejects([&] { engine.generate({message}, invalid, [](const std::string &) { return true; }); }, "Invalid sampling accepted");
        common_chat_msg too_long = message; too_long.content = std::string(30000, 'x');
        rejects([&] { engine.generate({too_long}, s, [](const std::string &) { return true; }); }, "Context overflow accepted");
        engine.unload();
        rejects([&] { engine.generate({message}, s, [](const std::string &) { return true; }); }, "Generation after unload accepted");
        std::cout << "PASS: model format/special tokens, repeat/reset, cancellation, settings, overflow, unload\n";
    } catch (const std::exception & e) { std::cerr << "FAIL: " << e.what() << '\n'; return 1; }
}
