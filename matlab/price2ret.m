function [returns] = price2ret(prices)
    n = length(prices) - 1;
    returns = zeros(n, 1);
    for i=1:n
        returns(i) = (prices(i+1) - prices(i)) / prices(i);
    end
end